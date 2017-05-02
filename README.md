# JarSplice

This project is based on http://ninjacave.com/jarsplice, but it provides more features, for example command line parameter support.

How to use the console version of JarSplice:
	-j	Define here the jars, which should be compressed
	-n	Define here the natives, which should be compressed
	-o	Define here the output path and name
	-m	Define here the main class
	-v	Define here vm arguments

Example:
	-j a.jar b.jar -n x32.dll x32.so -o c.jar -m main.Main -v -Xms128m -Xmx512m

# JarSplice

Dieses Projekt basiert auf http://ninjacave.com/jarsplice, jedoch bietet sie mehr Features wie beispielsweise Befehlszeilenparameter-Unterstützung.
