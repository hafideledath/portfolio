# Duck Debugger
#### Video Demo: https://youtu.be/wCzVL2gXrHg
#### Description:

Welcome, to Duck Debugger! You are a duck, whose job is to help people write their code. Use the WASD or arrow keys to walk, and shift to sprint. Use the spacebar to interact with people, signs doors or mysterious purple portals! Have fun, and get coding.

The game uses Unity to help create an RPG world in which you are a duck, wandering around a village. It’s a small, but sweet game that puts into practice a lot of mechanics that I found interesting. Through making this project, I learnt to code in C#!

I didn’t start out by using Unity. Instead, I started with PyGame, which allows users to code games using Python. This turned out to be too slow and wasn’t efficient enough for my liking. Then, I tried out Löve, a game engine that uses Lua, after watching a CS50 seminar about using the engine. However, I faced many similar issues with the engine. That’s when I turned to Unity. There were still many problems with Unity, such as the unfamiliar programming language it uses, C#. But after taking CS50, and gaining the ability to learn languages faster and more efficiently, I gave it a try.

The biggest, most unique and hardest to code mechanic of the game is the code-tester. It checks if a Python function works as intended, without any bugs. I used IronPython, which allows C# and Python to work in tandem. I had to make a lot of installations and I was in unfamiliar territory, but in the end, it worked out!

I used many resources to create my game, such as the tileset “forest_” by analogStudios_, which I accessed from itch.io (https://analogstudios.itch.io/forest). I used a few episodes form the “Unity RPG Tutorial” by gamesplusjames on YouTube as a reference.

My project has tons of files– 6 different “scenes” (.unity files), 18 different C# files, and 1 Python file. In addition, it has lots of tiles and sprites imported from external sources. The 6 different scenes are of the village, 4 different houses, and an isolated island. The C# files have a lot of uses– some of them help NPCs talk and move, other help the main character move and interact with signs and doors, etc.. The Python file works in tandem with IronPython and another C# file to help check whether or not a Python function the user inputs works as intended, without any bugs.
`
Overall, this project was a large undertaking for me, but allowed me to experiment with different languages and allowed me to gain a deeper understanding of game development!