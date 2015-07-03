ENTRYPOINT = GUIStopwatch
JARFILE = GUIStopwatch.jar

.PHONY: all reset run clean

all: reset
	@javac $(ENTRYPOINT).java && jar cfe $(JARFILE) $(ENTRYPOINT) *.class

reset:
	@rm -f *.class $(JARFILE)

run: all
	@java -jar $(JARFILE)

clean:
	@rm -f *.class
