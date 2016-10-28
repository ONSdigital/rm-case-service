=====================================
===  Sample file for the Printer ====
=====================================
<#list actionRequests as actionRequest>
${actionRequest.iac},${(actionRequest.caseRef)!},,,,${(actionRequest.address.organisationName)!},${(actionRequest.address.line1)!},${(actionRequest.address.line2)!},${(actionRequest.address.locality)!},${(actionRequest.address.townName)!},${(actionRequest.address.postcode)!}
</#list>