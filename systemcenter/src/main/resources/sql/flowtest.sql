set @outFlag='';
set @error='';
set @flowCode='flowCode';
set @flowType='1';
set @currentStatus='1';
set @flowIsend=1;
set @outCode='outCode';
set @_creator='creator';
set @flowTitle='flowTitle';
set @flowUrl='flowUrl';
set @flowRemark='flowRemark';
set @nextOperatorStr='nextOperator';     
set @nextOperatorStatusStr='nextOperatorStatus';
CALL proc_flow_create(@outFlag,@error,@flowCode,@flowType,@currentStatus,@flowIsend,@outCode,@_creator,@flowTitle,@flowUrl,@flowRemark,@nextOperatorStr,@nextOperatorStatusStr);

SELECT @outFlag,@error;


set @outFlag='';
set @error='';
set @flowZid=1;
set @flowCode='flowCodeUpdate';
set @flowType='1';
set @fromStatus='1';
set @toStatus='2';
set @flowIsend=1;
set @_updator='updator';
set @flowRemark='flowRemarkUpdate';
set @nextOperatorStr='nextOperatorUpdate';     
set @nextOperatorStatusStr='nextOperatorStatusUpdate';


CALL proc_flow_changestatus(@outFlag,@error,@flowZid,@flowCode,@flowType,@fromStatus,@toStatus,@flowIsend,@_updator,@flowRemark,@nextOperatorStr,@nextOperatorStatusStr);

SELECT @outFlag,@error;