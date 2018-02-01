JAVAC := javac
JAR := jar
CLASSES := $(patsubst %.java,%.class,$(wildcard *.java))
BIN := ../AllThoseTerritories.jar
MANIFEST := META-INF/MANIFEST.MF

all: $(BIN)

$(BIN): $(CLASSES)
	$(JAR) cmf $(MANIFEST) $(BIN) *.class

%.class: %.java
	$(JAVAC) $<

clean:
	rm -f $(BIN) *.class

.PHONY: all clean
