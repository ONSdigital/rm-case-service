==============================
===  File for the Printer ====
==============================
ActionId, ResponseRequired, ActionType, IAC, Line1, Town, Postcode
<#list actionRequests as actionRequest>
${actionRequest.actionId},${actionRequest.actionType},${actionRequest.iac},${(actionRequest.address.line1)!},${(actionRequest.address.line2)!},${(actionRequest.address.locality)!},${(actionRequest.address.townName)!},${(actionRequest.address.postcode)!}
</#list>