ConverterUtf8ToCp1252
=====================

Some UTF8 Tools.

ConvertUtf8toCp1252
-------------------

Mass convertion of all files under the rootpath from one charset to another.
By default convert from utf8 to Cp1252. 
 * Modified files are backuped in a directory "ubak".
 * Hard coded excludes : 
 ** directories : abak, ubak, .git
 ** files : *.zip, *.bak, *.jar

Usage : 
 '''java convert.ConvertUtf8toCp1252 "<<files RootPath>>" <optional:fromCharset> <optional:toCharset>'''
 

Utf8BadAccentsFillDico
----------------------
Extract a dictionnary of all word with bad encoding accents (show as ï¿½ or ? in utf8).
The properties files could be corrected with a spelling tools.
Mass replace from dictionnary could be done with 'Utf8AccentsCorrecter'

Usage : 
 '''java Utf8BadAccentsFillDico "<<files RootPath>>"  > myDico.properties'''


Utf8AccentsCorrecter
--------------------
Read a .properties of all words to replace and replace them in all files under the rootpath.
 * Modified files are backuped in a directory "abak".
 * Hard coded excludes : 
 * - directories : abak, ubak, .git
 * - files : *.zip, *.bak, *.jar
 
Usage : 
 '''java Utf8AccentsCorrecter "<<files RootPath>>" "<<dico.properties path>>"'''


License
-------
Copyright 2013 - Nicolas Piedeloup

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Version history
---------------

v1.0
- first release
