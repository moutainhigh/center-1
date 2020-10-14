package com.cmall.systemcenter.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * redis缓存数据库增删改查及加锁、通道存储工具类
 * 
 * @author xiegj
 * */
public class jedisUtil extends BaseClass {

	abstract class Executor<T> {
		Jedis jedis;
		JedisPool pool;

		public Executor() {
			this.pool = new JedisPool(new JedisPoolConfig(), getHost());
			this.jedis = pool.getResource();
			jedis.auth(getPwd());
		}

		abstract T execute();

		/**
		 * 调用{@link #execute()}并返回执行结果 它保证在执行{@link #execute()}
		 * 之后释放数据源returnResource(jedis)
		 * 
		 * @return 执行结果
		 */
		public T getResult() {
			T result = null;
			try {
				result = execute();
			} catch (Throwable e) {
				throw new RuntimeException("Redis出现错误：", e);
			} finally {
				if (jedis != null) {
					pool.returnResource(jedis);
				}
			}
			return result;
		}
	}

	/****************************************************** String字符串类型key的相关操作 start *********************************************************************/
	/**
	 * 将字符串值 value 关联到 key 。 如果 key 已经持有其他值， setString 就覆写旧值，无视类型。
	 * 对于某个原本带有生存时间（TTL）的键来说， 当 setString 成功在这个键上执行时， 这个键原有的 TTL 将被清除。
	 * 
	 * @param key
	 * @param value
	 * @return 在设置操作成功完成时，才返回 OK 。
	 */
	public String setString(final String key, final String value) {
		return new Executor<String>() {
			String execute() {
				return jedis.set(key, value);
			}
		}.getResult();
	}

	/**
	 * 返回 key 所关联的字符串值。如果 key 不存在那么返回特殊值 nil 。 假如 key 储存的值不是字符串类型，返回一个错误，因为
	 * getString 只能用于处理字符串值。
	 * 
	 * @param key
	 * @return 当 key 不存在时，返回 nil ，否则，返回 key 的值。如果 key 不是字符串类型，那么返回一个错误。
	 */
	public String getString(final String key) {
		return new Executor<String>() {
			String execute() {
				return jedis.get(key);
			}
		}.getResult();
	}

	/**
	 * 批量的 {@link #setString(String, String)}
	 * 
	 * @param pairs
	 *            键值对数组{数组第一个元素为key，第二个元素为value}
	 * @return 操作状态的集合
	 */
	public List<Object> batchSetString(final List<Pair<String, String>> pairs) {
		return new Executor<List<Object>>() {

			@Override
			List<Object> execute() {
				Pipeline pipeline = jedis.pipelined();
				for (Pair<String, String> pair : pairs) {
					pipeline.set(pair.getKey(), pair.getValue());
				}
				return pipeline.syncAndReturnAll();
			}
		}.getResult();
	}

	/**
	 * 批量的 {@link #getString(String)}
	 * 
	 * @param keys
	 *            key数组
	 * @return value的集合
	 */
	public List<String> batchGetString(final String[] keys) {
		return new Executor<List<String>>() {

			@Override
			List<String> execute() {
				Pipeline pipeline = jedis.pipelined();
				List<String> result = new ArrayList<String>(keys.length);
				List<Response<String>> responses = new ArrayList<Response<String>>(
						keys.length);
				for (String key : keys) {
					responses.add(pipeline.get(key));
				}
				pipeline.sync();
				for (Response<String> resp : responses) {
					result.add(resp.get());
				}
				return result;
			}
		}.getResult();
	}

	/**
	 * 删除模糊匹配的key
	 * 
	 * @param likeKey
	 *            模糊匹配的key
	 * @return 删除成功的条数
	 */
	public long delKeysLike(final String likeKey) {
		return new Executor<Long>() {
			@Override
			Long execute() {
				Collection<String> keys = jedis.keys(likeKey + "*");
				Iterator<String> iter = keys.iterator();
				long count = 0;
				while (iter.hasNext()) {
					String key = iter.next();
					count += jedis.del(key);
				}
				return count;
			}
		}.getResult();
	}

	/**
	 * 删除
	 * 
	 * @param key
	 *            匹配的key
	 * @return 删除成功的条数
	 */
	public Long delKey(final String key) {
		return new Executor<Long>() {

			@Override
			Long execute() {
				return jedis.del(key);
			}
		}.getResult();
	}

	/****************************************************** String字符串类型的value的相关操作 end *********************************************************************/
	/****************************************************** linkList链表类型的value的相关操作 start *********************************************************************/

	/**
	 * 一次获得多个链表的数据
	 * 
	 * @param keys
	 *            key的数组
	 * @return 执行结果
	 */
	public Map<String, List<String>> batchGetAllList(final List<String> keys) {
		return new Executor<Map<String, List<String>>>() {

			@Override
			Map<String, List<String>> execute() {
				Pipeline pipeline = jedis.pipelined();
				Map<String, List<String>> result = new HashMap<String, List<String>>();
				List<Response<List<String>>> responses = new ArrayList<Response<List<String>>>(
						keys.size());
				for (String key : keys) {
					responses.add(pipeline.lrange(key, 0, -1));
				}
				pipeline.sync();
				for (int i = 0; i < keys.size(); ++i) {
					result.put(keys.get(i), responses.get(i).get());
				}
				return result;
			}
		}.getResult();
	}

	/**
	 * 返回list所有元素
	 * 
	 * @param key
	 *            key
	 * @return list所有元素
	 */
	public List<String> listGetAll(final String key) {
		return new Executor<List<String>>() {

			@Override
			List<String> execute() {
				return jedis.lrange(key, 0, -1);
			}
		}.getResult();
	}

	/**
	 * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
	 * 
	 * @param key
	 *            key
	 * @param values
	 *            value的数组
	 * @return 执行 listPushTail 操作后，表的长度
	 */
	public Long listPushTail(final String key, final String... values) {
		return new Executor<Long>() {

			@Override
			Long execute() {
				return jedis.rpush(key, values);
			}
		}.getResult();
	}

	/**
	 * 将一个或多个值 value 插入到列表 key 的表头
	 * 
	 * @param key
	 *            key
	 * @param value
	 *            string value
	 * @return 执行 listPushHead 命令后，列表的长度。
	 */
	public Long listPushHead(final String key, final String value) {
		return new Executor<Long>() {

			@Override
			Long execute() {
				return jedis.lpush(key, value);
			}
		}.getResult();
	}

	/**
	 * 将一个或多个值 value 插入到列表 key 的表头, 当列表大于指定长度是就对列表进行修剪(trim)
	 * 
	 * @param key
	 *            key
	 * @param value
	 *            string value
	 * @param size
	 *            链表超过这个长度就修剪元素
	 * @return 执行 listPushHeadAndTrim 命令后，列表的长度。
	 */
	public Long listPushHeadAndTrim(final String key, final String value,
			final long size) {
		return new Executor<Long>() {

			@Override
			Long execute() {
				Pipeline pipeline = jedis.pipelined();
				Response<Long> result = pipeline.lpush(key, value);
				// 修剪列表元素, 如果 size - 1 比 end 下标还要大，Redis将 size 的值设置为 end 。
				pipeline.ltrim(key, 0, size - 1);
				pipeline.sync();
				return result.get();
			}
		}.getResult();
	}

	/**
	 * 返回指定区间内的元素，下标从0开始，负值表示从后面计算，-1表示倒数第一个元素，key不存在返回空列表
	 * 
	 * @param key
	 *            key
	 * @param beginIndex
	 *            下标开始索引（包含）
	 * @param endIndex
	 *            下标结束索引（不包含）
	 * @return 指定区间内的元素
	 */
	public List<String> listRange(final String key, final long beginIndex,
			final long endIndex) {
		return new Executor<List<String>>() {

			@Override
			List<String> execute() {
				return jedis.lrange(key, beginIndex, endIndex - 1);
			}
		}.getResult();
	}

	/****************************************************** linklist链表类型的value的相关操作 end *********************************************************************/
	/****************************************************** hash哈希类型的value的相关操作 start *********************************************************************/
	/**
	 * 返回哈希表 key 中给定域 field 的值。
	 * 
	 * @param key
	 *            key
	 * @param field
	 *            域
	 * @return 给定域的值。当给定域不存在或是给定 key 不存在时，返回 nil 。
	 */
	public String hashGet(final String key, final String field) {
		return new Executor<String>() {

			@Override
			String execute() {
				return jedis.hget(key, field);
			}
		}.getResult();
	}

	/**
	 * 同时将多个 field-value (域-值)对设置到哈希表 key 中。
	 * 
	 * @param key
	 *            key
	 * @param hash
	 *            field-value的map
	 * @return 如果命令执行成功，返回 OK 。当 key 不是哈希表(hash)类型时，返回一个错误。
	 */
	public String hashMultipleSet(final String key,
			final Map<String, String> hash) {
		return new Executor<String>() {

			@Override
			String execute() {
				return jedis.hmset(key, hash);
			}
		}.getResult();
	}

	/**
	 * 返回哈希表 key 中，所有的域和值。在返回值里，紧跟每个域名(field
	 * name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
	 * 
	 * @param key
	 * @return 以列表形式返回哈希表的域和域的值。若 key 不存在，返回空列表。
	 */
	public Map<String, String> hashGetAll(final String key) {
		return new Executor<Map<String, String>>() {

			@Override
			Map<String, String> execute() {
				return jedis.hgetAll(key);
			}
		}.getResult();
	}

	/**
	 * 批量的{@link #hashGetAll(String)}
	 * 
	 * @param keys
	 *            key的数组
	 * @return 执行结果的集合
	 */
	public List<Map<String, String>> batchHashGetAll(final String... keys) {
		return new Executor<List<Map<String, String>>>() {

			@Override
			List<Map<String, String>> execute() {
				Pipeline pipeline = jedis.pipelined();
				List<Map<String, String>> result = new ArrayList<Map<String, String>>(
						keys.length);
				List<Response<Map<String, String>>> responses = new ArrayList<Response<Map<String, String>>>(
						keys.length);
				for (String key : keys) {
					responses.add(pipeline.hgetAll(key));
				}
				pipeline.sync();
				for (Response<Map<String, String>> resp : responses) {
					result.add(resp.get());
				}
				return result;
			}
		}.getResult();
	}

	/**
	 * 批量的{@link #hashMultipleGet(String, String...)}，与
	 * {@link #batchHashGetAll(String...)}不同的是，返回值为Map类型
	 * 
	 * @param keys
	 *            key的数组
	 * @return 多个hash的所有filed和value
	 */
	public Map<String, Map<String, String>> batchHashGetAllForMap(
			final String... keys) {
		return new Executor<Map<String, Map<String, String>>>() {

			@Override
			Map<String, Map<String, String>> execute() {
				Pipeline pipeline = jedis.pipelined();

				// 设置map容量防止rehash
				int capacity = 1;
				while ((int) (capacity * 0.75) <= keys.length) {
					capacity <<= 1;
				}
				Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>(
						capacity);
				List<Response<Map<String, String>>> responses = new ArrayList<Response<Map<String, String>>>(
						keys.length);
				for (String key : keys) {
					responses.add(pipeline.hgetAll(key));
				}
				pipeline.sync();
				for (int i = 0; i < keys.length; ++i) {
					result.put(keys[i], responses.get(i).get());
				}
				return result;
			}
		}.getResult();
	}

	/**
	 * 批量的{@link #hashMultipleSet(String, Map)}，在管道中执行
	 * 
	 * @param data
	 *            Map<String, Map<String, String>>格式的数据
	 * @return 操作状态的集合
	 */
	public List<Object> batchHashMultipleSet(
			final Map<String, Map<String, String>> data) {
		return new Executor<List<Object>>() {

			@Override
			List<Object> execute() {
				Pipeline pipeline = jedis.pipelined();
				for (Map.Entry<String, Map<String, String>> iter : data
						.entrySet()) {
					pipeline.hmset(iter.getKey(), iter.getValue());
				}
				return pipeline.syncAndReturnAll();
			}
		}.getResult();
	}

	/**
	 * 返回哈希表 key 中，一个或多个给定域的值。如果给定的域不存在于哈希表，那么返回一个 nil 值。 (N为fields的数量)
	 * 
	 * @param key
	 *            key
	 * @param fields
	 *            field的数组
	 * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
	 */
	public List<String> hashMultipleGet(final String key,
			final String... fields) {
		return new Executor<List<String>>() {

			@Override
			List<String> execute() {
				return jedis.hmget(key, fields);
			}
		}.getResult();
	}

	/****************************************************** hash哈希类型的value的相关操作 end *********************************************************************/
	/****************************************************** SortedSet有序集合类型的value的相关操作 start ************************************************************/

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。 有序集成员按
	 * score 值递减(从大到小)的次序排列。
	 * 
	 * @param key
	 *            key
	 * @param max
	 *            score最大值
	 * @param min
	 *            score最小值
	 * @return 指定区间内，带有 score 值(可选)的有序集成员的列表
	 */
	public Set<String> revrangeByScoreWithSortedSet(final String key,
			final double max, final double min) {
		return new Executor<Set<String>>() {
			@Override
			Set<String> execute() {
				return jedis.zrevrangeByScore(key, max, min);
			}
		}.getResult();
	}

	/**
	 * 将一个 member 元素及其 score 值加入到有序集 key 当中。
	 * 
	 * @param key
	 *            key
	 * @param score
	 *            score 值可以是整数值或双精度浮点数。
	 * @param member
	 *            有序集的成员
	 * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
	 */
	public Long addWithSortedSet(final String key, final double score,
			final String member) {
		return new Executor<Long>() {

			@Override
			Long execute() {
				return jedis.zadd(key, score, member);
			}
		}.getResult();
	}

	/**
	 * 将多个 member 元素及其 score 值加入到有序集 key 当中。
	 * 
	 * @param key
	 *            key
	 * @param scoreMembers
	 *            score、member的pair
	 * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
	 */
	public int addWithSortedSet(final String key,
			final Map<Double, String> scoreMembers) {
		return 0;
	}

	/****************************************************** SortedSet有序集合类型的value的相关操作 end *********************************************************************/

	/**
	 * 根据配置文件获取redis服务器的地址
	 * 
	 * @return 服务器地址
	 */
	public String getHost() {
		String sAddresString = bConfig("systemcenter.redisaddress");
		return sAddresString;
	}

	/**
	 * 根据配置文件获取redis数据库的密码
	 * 
	 * @return 数据库密码
	 * */
	public String getPwd() {
		String pwd = bConfig("systemcenter.redispwd");
		return pwd;
	}

	/**
	 * 构造Pair键值对
	 * 
	 * @param key
	 *            key
	 * @param value
	 *            value
	 * @return 键值对
	 */
	public <K, V> Pair<K, V> makePair(K key, V value) {
		return new Pair<K, V>(key, value);
	}

	/**
	 * 键值对
	 * 
	 * @version V1.0
	 * @param <K>
	 *            key
	 * @param <V>
	 *            value
	 */
	public class Pair<K, V> {

		private K key;
		private V value;

		public Pair(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}
	}
}
