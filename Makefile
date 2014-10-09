JFLAGS = -g
JC = javac
CLASSPATH = ./dist/AIMA.jar:./dist/Desastres.jar:.
SRCPATH = IA/desastres
DOC = ./javadoc

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java -cp $(CLASSPATH)

CLASSES = \
		$(SRCPATH)/DesastresGoalTest.java \
		$(SRCPATH)/DesastresHeuristicFunction.java \
		$(SRCPATH)/DesastresMain.java \
		$(SRCPATH)/DesastresState.java \
		$(SRCPATH)/DesastresSuccessorFunction.java

default: classes

classes: $(CLASSES:.java=.class) 

run: jar
		java -jar ia_desastres.jar

jar: classes
		jar cvfe ia_desastres.jar IA.desastres.DesastresMain $(SRCPATH)/*.class

$(DOC):
		mkdir $(DOC)

docs: $(DOC) $(CLASSES)
		javadoc -version -author -d $(DOC) $(CLASSES) 

clean:
		$(RM) -r $(SRCPATH)/*.class ia_desastres.jar $(DOC)
