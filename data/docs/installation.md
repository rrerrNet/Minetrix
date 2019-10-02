---
title: "Installation"
priority: 0
---

Installing Minetrix should be super easy.

The first steps are as with any other plug-in:

1. Download the prebuilt `.jar`
2. Place it in the `plugins/Minetrix` directory of your Spigot/PaperMC server
   (you may need to create them if they do not exist yet)
3. Start/restart/reload your server

This will drop the default [configuration][config] file to
`plugins/Minetrix/config.yml`.  Open it up with a text editor of your choice,
and change the following:

- `homeserver.domain` should point to the Matrix homeserver you're using
- `username` is the Matrix user ID of your bot user
- `password` is the password of your bot user in plain text.  Don't worry, this
  is only needed once and can be safely removed once the bot user successfully
  authenticated against the homeserver
- `room_id` is the Matrix room ID where the bot user should reside in.  You can
  find the room ID within Riot in the advanced room settings.  
  **Wichtig**: You have to invite the bot user to the room beforehand.

Once changed, reload or restart your server again.  If everything worked well,
you should see the following in your server logs:

```
[13:37:46 INFO]: [Minetrix] Enabling Minetrix v0.1.0
[13:37:46 INFO]: [Minetrix] Starting up Minetrix
[13:37:46 INFO]: [Minetrix] Registering Matrix roomMessageReceived event listener
[13:37:46 INFO]: [Minetrix] Starting Matrix event queue
[13:37:46 INFO]: [Minetrix] Performing initial Matrix sync
[13:37:47 INFO]: [Minetrix] Starting Matrix sync loop
[13:37:47 INFO]: [Minetrix] Registering net.rrerr.minetrix.MinecraftChatMessageListener
[13:37:47 INFO]: [Minetrix] Registering net.rrerr.minetrix.PlayerEventListener
[13:37:47 INFO]: [Minetrix] Minetrix started up
```

You may now open up the configuration file again and set the password field to
an empty string (i.e. `""`) since the bot now authenticates using the received
access token.

[config]: ../configuration
