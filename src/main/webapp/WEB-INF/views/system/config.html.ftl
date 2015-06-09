<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>ATB Hypermedia Seed - Configuration</title>
        <meta charset="utf-8" />
    </head>
    <body>
        <#assign propkeys = properties?keys?sort>
        <ul>
        <#list propkeys as propkey>
            <li>${propkey}: ${properties[propkey]}</li>
        </#list>
        </ul>
    </body>
</html>
