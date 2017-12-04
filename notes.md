### General idea
* The idea was to attempt to adhere to both the printed paper requirement from 1S-2.009 via a final printing process and also utilize the validity of electronic signatures to satisfy the "original signature" requirement from 1S-2.0091. In simple language a user would fill out the name and address information via an online form, likely text based but possibly using handwriting. Then, the user would create a handwriting based signature. So on a phone, this would be using your finger to draw out your signature. Upon submission, the official form would be filled out with all the fields matched up to the required places. Finally, to satisfy the printing requirement, the digitally signed document would be printed out and mailed or hand delivered to the Supervisor of Elections in the registered voter's county. The end product would represent the intent of the voter to support the initiative and look identical to a document prepared via more traditional methods by the time the Supervisor of Elections received it.

### Specific notes and ideas
* Current idea is to build a java web servlet which can take a simple form submission containing the required address and name fields and generate the corresponding pdf file.
* Additional validation
  * approximate zip code of submission ip
  * require location to be enabled in the browser/mobile app and presence in florida
  * basic validation of address
  * Ensure all fields are filled out
* Digitally sign the resulting pdf

### Tomcat setup
* Made a new directory under webapps, voteinit
* Copied itext5-itextpdf-5.5.12.jar, itext5-itext-pdfa.5.5.12.jar, itext5-itext-xtra-5.5.12.jar, itext5-xmlworker-5.5.12.jar into voteinit\WEB-INF\lib
* Setup basic plumbing by modifying the voteinit\WEB-INF\web.xml

### Current status
* Making a simple example to write a pdf dynamically that can be downloaded immediately