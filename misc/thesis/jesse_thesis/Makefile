# Put user-specific changes in your own Makefile.user.
# Make will silently continue if that file does not exist.
-include Makefile.user

TARGET   = paper

# To regenerate, run:  latex-process-inputs -makefilelist paper.tex
TEXFILES = \
paper.tex \
abstract.tex \
introduction.tex \
previousWork.tex \
representation.tex \
evaluation.tex \
generalizing.tex \
conclusion.tex

FIGURES = shopping_cart \
motivating-log \
performanceLogLines \
language-venn \
invar_synthetic \
union \
tails \
ktails \
invRegexDfa \
spurious
PDF_FIGURES = $(addprefix fig/, $(addsuffix .pdf, $(FIGURES)))

CLSFILES = $(wildcard *.cls)

PSTOPDF ?= pstopdf

.SUFFIXES:	.plot .eps .pdf

all: $(TARGET).pdf #

$(TARGET).pdf: ${PDF_FIGURES} $(TARGET).bbl $(TEXFILES) $(CLSFILES)
	pdflatex $(TARGET).tex
	pdflatex $(TARGET).tex

$(TARGET).bbl: $(TARGET).bib $(TEXTFILES)
	pdflatex $(TARGET).tex
	bibtex $(TARGET)
	pdflatex $(TARGET).tex
	pdflatex $(TARGET).tex

tags: TAGS
.PHONY: tags
TAGS: ${TEXFILES}
	etags $^

clean:
	rm $(TARGET).pdf $(TARGET).log $(TARGET).blg $(TARGET).aux $(TARGET).bbl $(TARGET).out
