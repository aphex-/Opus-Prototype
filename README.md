# Opus prototype 
This is a tool to generate infinite landscapes by combining noise artifacts. 
You can use the editor to create a world and use the library to import it to your own application. 
Opus prototype offers you a way to easily describe landscapes (like seen in Minecraft) 
with values that are based on pseudo random noise algorithms.

A mac and windows build of the editor can be loaded here: http://nuke-the-moon.itch.io/opus

A short preview / tutorial of the functionality: https://youtu.be/3owuS5VRnAM

This library is used for the game: http://nuke-the-moon.itch.io/alien-ark
And for the game: http://nuke-the-moon.com/pioneerskies/index.html
Created by www.nuke-the-moon.com

For more information about Opus Prototype read the documentation: editor/data/docu/index.html

This repo was released in a hurry. ~~The library is work in progress.~~


Licenced under the Apache License Version 2.0

## How to load a saved world ###
To load a chunk of a saved world you have to import the base library *opuslib-0.0.1a.jar* and *opusloaderjson-0.0.1a.jar* to read the json format.
```java
// create a loder for json files
OpusLoaderJson opusJsonLoader = new OpusLoaderJson();
byte[] bytes = opusjsonLoader.readFile("worlds/NewWorld.json");
// load opus based on a saved world
Opus opus = opusjsonLoader.load(new Samplers(), new Algorithms(), bytes);
// create a chunk at x:0 y:0 with the standard resolution 1
Chunk chunk = opus.createChunk(0, 0, 1);
```
The chunk now contains a map of height values. The instance of opus contains layer and interpreter information.
