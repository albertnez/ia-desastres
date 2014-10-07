JFLAGS = -g
JC = javac
CLASSPATH = ./dist/AIMA.jar:./dist/Desastres.jar
SRCPATH = src/IA/desastres
BINPATH = ./bin

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java -cp $(CLASSPATH) -d $(BINPATH)

CLASSES = \
		$(SRCPATH)/DesastresGoalTest.java \
		$(SRCPATH)/DesastresHeuristicFunction.java \
		$(SRCPATH)/DesastresMain.java \
		$(SRCPATH)/DesastresState.java \
		$(SRCPATH)/DesastresSuccessorFunction.java

default: classes

classes: $(BINPATH) $(CLASSES:.java=.class) 

$(BINPATH):
		mkdir $(BINPATH)

run: 
		cd $(BINPATH)/IA/desastres/
		java DesastresMain
jar:
		jar cvf ia_desastres.jar $(BINPATH)/IA/desastres/*.class

clean:
		$(RM) -r $(BINPATH) ia_desastres.jar
