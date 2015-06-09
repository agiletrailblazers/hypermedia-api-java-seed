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
        li{display:block;margin-top:4px;} .descr{font-style:italic;display:block} .value:before{content:'= '} input{width:250px}
        </style>
</head>
<body>
    <ul class='attrs'>
    <#list attributeMetadata?keys as attributeName>
        <li>
            <span class='descr'>${attributeMetadata[attributeName].description}</span>
            <span class='name'>${attributeMetadata[attributeName].type}<b> <a href='value?objectName=${objectName?url}&amp;attributeName=${attributeName?url}'>${attributeName}</a></b></span>
            <span class='value'>${attributeValueMap[attributeName]!?string}</span>
        </li>
        <br/>
    </#list>
    </ul>
    <ul class='ops'>
    <#list operationMetadata?keys as opName>
        <li>
            <span class='descr'>${operationMetadata[opName].description}</span>
            <span class='name'>${operationMetadata[opName].returnType} <a href='info?objectName=${objectName?url}&amp;operationName=${opName?url}'>${opName}</a>(<#list operationMetadata[opName].signature as paramInfo>${paramInfo.type} <b>${paramInfo.name}</b><#if paramInfo.description?length &gt; 0> <i>${paramInfo.description}</i></#if><#if paramInfo_has_next>,</#if></#list>)</span>
        </li>
        <br/>
    </#list>
    </ul>
</body>
</html>
