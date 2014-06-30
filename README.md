boxscores
=========

Generating a box-score-like visualization of Dota2 matches. Makes extensive use of skadistats/clarity for processing replays to extract the information we need to make a visualization.

This repository has been basically replaced by functionality in the main drewww/dota2results repo. This repo still works for extracting basic information from dota2replay files, but the addition of scoreboard information to the API made it possible to get decent low-res data from the web api. This is way more straightforward than having to download replays, run a full parse, and then generate images from that data. 
