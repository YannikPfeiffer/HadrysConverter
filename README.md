# Hadrys-Converter

Dies ist eine  einfache Anwendung, um Aufgaben aus den PDF-Dokumenten der Übungen von Hadrys zu extrahieren und in eine formatierte
Word-Datei zu schreiben.
 
## Benutzte Frameworks
* Apache pdfBox
* Apache POI
* OpenJFX
* Lombok
* Jackson
* RichTextFx

## Lokale Entwicklung
Eine Java Version 11+ ist erforderlich.

### Lokales Starten
Um das Programm lokal zu starten wird das Kommando `mvn clean javafx:run` benutzt.
Außerdem müssen die folgenden dependencies auskommentiert werden.
````xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-graphics</artifactId>
    <version>13</version>
    <classifier>win</classifier>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-graphics</artifactId>
    <version>13</version>
    <classifier>mac</classifier>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-graphics</artifactId>
    <version>13</version>
    <classifier>linux</classifier>
</dependency>          
````

### Lokales Bauen
Um lokal eine JAR-Datei zu bauen wird `mvn clean package` benutzt.


## Problembehandlung
### Unrecognized option: --module-path
Tritt dieser Fehler auf wird eine veraltete Version von Java benutzt. Hier kann es helfen, die veraltete JRE-Version zu deinstallieren und stattdessen eine Version von 11 oder höher zu benutzen.

### Unsupported major.minor version 55.0 maven
Auch hier wird eine alte Version von Java genutzt. Bitte installiere Java 11 oder höher.
