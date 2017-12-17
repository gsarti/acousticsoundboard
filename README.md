# A17GEP_Equipe8 - Acoustic Soundboard

## Membres
* Tristan Deschamps
* Gabriele Sarti

## Vision du projet
Notre projet final pour le cours de Développement d'applications en mobilité sera constitué par une application permettant de faire jouer des fichier audio stockés localement.

 L'application sera constituée en partant par un ensemble de fichiers audio par défaut. De plus, il sera possible d'ajouter d'autres traces sonores en important à partir du stockage local, ou en les téléchargeant de l'Internet. 
 
 Finalement, l'application permettra de créer un fichier à partir de l'audio du microphone. Son utilisation est plutôt récreative, mais il pourrait être utilisé pour conserver des notes verbales ou des chansons.

## Utilisation des Notions évaluées
1. ##	RecyclerView
- [x] Permettre de visualiser l'ensemble des traces audio dans l'application.
2. ##	Préférences Android
- [x] Changer le thème de l'application
- [x] Modifier la chanson par defaut du widget.
3. ## SQLite
- [x] Stocker l'information supplémentaire associée aux fichiers audio (image, description, date).
4. ##	Content Provider (mode source et client)
- [x] D'autres application pourront visualiser les informations des audio contenus dans l'application.
- [x] Utiliser le provier en tant qu'interface interne avec la base de données.
5. ##	Service et notifications
- [x] Permettre de contrôler l'audio player de l'application.
- [x] Notifications sur le changement d'un favori
6. ##	DataBinding
- [x] Gérer l'affichage du nom des traces audio.
7. ##	Fragments (doit fonctionner dans les deux orientations, en mode Nexus9 et NexusOne)
- [ ] Permettre un affichage simplifié en portrait et un affichage detaillé (l'image, la description...) dans l'orientation paysage.
8. ##	Widgets
- [x] Faire jouer un son favori à partir de l'écran d'accueil Android.
9. ##	Manifeste et ressources (Icônes applicatives et multilinguisme français+anglais)
- [] Multilangue en francais et en anglais.
- [x] Icône personnalisée pour l'application.
10. ##	Géolocalisation (mode au choix)
- [ ] Geofencing pour démarrer un audio au cégep
