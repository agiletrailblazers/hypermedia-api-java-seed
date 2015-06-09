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
    <#if value??>${value}</#if>
</body>
</html>
