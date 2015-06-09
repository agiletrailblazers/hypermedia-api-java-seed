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
    <ul>
        <#list mbeans as mbean>
            <li><a href='bean?objectName=${mbean?url}'>${mbean}</a></li>
        </#list>
    </ul>
</body>
</html>
</#escape>
