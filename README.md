#ConverterUtf8ToCp1252

A set of UTF8 Tools.
For correcting bad encoded sources files (and other usage :))


###ConvertUtf8toCp1252
Mass convertion of all files under the rootpath from one charset to another.
By default convert from utf8 to Cp1252. 
 * Modified files are backuped in a directory "ubak".
 * Hard coded excludes : 
   + directories : abak, ubak, .git
   + files : *.zip, *.bak, *.jar

Usage : 
`java convert.ConvertUtf8toCp1252 "<<files RootPath>>" optional:fromCharset> <optional:toCharset>`
 
 

###Utf8BadAccentsFillDico
Extract a dictionnary of all word with bad encoding accents (show as ï¿½ or ? in utf8).
The properties files could be corrected with a spelling tools.
Mass replace from dictionnary could be done with 'Utf8AccentsCorrecter'

Usage : 
`java convert.Utf8BadAccentsFillDico "<<files RootPath>>"  > myDico.properties`



###Utf8AccentsCorrecter
Read a .properties of all words to replace and replace them in all files under the rootpath.
 * Modified files are backuped in a directory "abak".
 * Hard coded excludes : 
   + directories : abak, ubak, .git
   + files : *.zip, *.bak, *.jar
 
Usage : 
`java convert.Utf8AccentsCorrecter "<<files RootPath>>" "<<myDico (no extention) path>>"`


###License : AGPLv3
     UTF8 Tools : ConverterUtf8ToCp1252, Utf8BadAccentsFillDico, Utf8AccentsCorrecter
     Copyright (C) 2013 - Nicolas Piedeloup
     
     This program is free software: you can redistribute it and/or modify
     it under the terms of the GNU Affero General Public License as
     published by the Free Software Foundation, either version 3 of the
     License, or (at your option) any later version.
     
     This program is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU Affero General Public License for more details.
     
     You should have received a copy of the GNU Affero General Public License
     along with this program.  If not, see <http://www.gnu.org/licenses/>.


###Version history
####v1.0
- first release
