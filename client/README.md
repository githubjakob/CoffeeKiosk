# Client

Bitte local.properties nicht in Git hinzufügen.

## Google Push Notifications
Dazu gibt es die beiden Services InstanceIdService und MessagingService, beide sind im Manifest
registriert.
Der InstanceIdService erzeugt beim App-Start ein Token, dieses Token identifiziert unsere App/das Gerät (
wird als MyRefreshedToken gelogt).
Wenn man das Token kennt, kann man von der Google Webseite aus
https://console.firebase.google.com/project/projectblackalert/notification dem Gerät eine Push Nachricht schicken.
