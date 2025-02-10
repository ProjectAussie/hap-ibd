sources = $(wildcard src/*/*.java)
classes = $(sources:.java=.class)

all: hapIbdMain hap-ibd.jar

hapIbdMain: $(classes)

%.class : %.java
	javac -cp src/ $<

hap-ibd.jar: hapIbdMain
	jar cfe hap-ibd.jar hapibd/HapIbdMain -C src/ ./

clean :
	rm -f src/*/*.class hap-ibd.jar
