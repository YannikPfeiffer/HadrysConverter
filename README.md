# Hadrys-Converter

Dies ist eine  einfache Anwendung, um Aufgaben aus den PDF-Dokumenten der Übungen von Hadrys zu extrahieren und in eine formatierte
Word-Datei zu schreiben.
 
## Benutzte Frameworks
* Apache pdfBox
* Apache POI
* OpenJFX

## Lokale Entwicklung
Um das Programm lokal zu starten wird das Kommando `mvn clean javafx:run` benutzt.

Um lokal eine JAR-Datei zu bauen wird `mvn clean package` benutzt.

Eine Java Version 11+ ist erforderlich.

## Problembehandlung
### Unrecognized option: --module-path
Tritt dieser Fehler auf wird eine veraltete Version von Java benutzt. Hier kann es helfen, die veraltete JRE-Version zu deinstallieren und stattdessen eine Version von 11 oder höher zu benutzen.

### Unsupported major.minor version 55.0 maven
Auch hier wird eine alte Version von Java genutzt. Bitte installiere Java 11 oder höher.
