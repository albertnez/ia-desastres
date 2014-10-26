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
		$(SRCPATH)/DesastresSuccessorFunction.java \
		$(SRCPATH)/DesastresSuccessorFunctionSA.java

default: classes

classes: $(CLASSES:.java=.class) 

run: jar
		java -jar ia_desastres.jar 5 1 100 1234 1.0 1

jar: classes
		jar cvfm ia_desastres.jar Manifest.txt $(SRCPATH)/*.class

$(DOC):
		mkdir $(DOC)

docs: $(DOC) $(CLASSES)
		javadoc -version -author -d $(DOC) $(CLASSES) 

clean:
		$(RM) -r $(SRCPATH)/*.class ia_desastres.jar $(DOC)
