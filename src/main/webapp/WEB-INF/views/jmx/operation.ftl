<#escape x as x?html>
<#-- Use UTF-8 charset for URL escaping from now: -->
<#setting url_escaping_charset="UTF-8" />
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>ATB Hypermedia Seed</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <style>
        *[itemprop]::before {
        content: attr(itemprop) ": ";
        text-transform: capitalize;
        }
        li{display:block;margin-top:4px;} .descr{font-style:italic;display:block} .value:before{content:'='} input{width:250px}
        </style>
</head>
<body>
    <h1>${objectName}.${operationName}</h1>
    <form action='invoke?objectName=${objectName?url}&amp;operationName=${operationName?url}' method='POST'>
        <table border='0'>
            <#list operationInfo.signature as parameterInfo>
                <tr>
                    <td>${parameterInfo.type} ${parameterInfo.name}</td>
                    <td><input name='${parameterInfo.name}'/></td>
                    <td><i>${parameterInfo.description}</i></td>
                </tr>
            </#list>
        </table><input type='submit'/>
    </form>
</body>
</html>
</#escape>
