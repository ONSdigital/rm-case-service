==============================
===  File for the Printer ====
==============================
ActionId, ResponseRequired, ActionType, IAC
<#list actionRequests as actionRequest>
	${actionRequest.actionId}, ${actionRequest.actionType}, ${actionRequest.iac}
</#list>