# Bamse
## Universal Re-frame setup running on Node.js

I added a re-frame frontend and passed the state from the server to the frontend.
Work in progress :-)

## Used libraries and features
* [clojurescript](https://clojurescript.org/)
* [re-frame](https://github.com/Day8/re-frame)
* [shadow-cljs](https://github.com/thheller/shadow-cljs)
* [devcards](https://github.com/bhauman/devcards)
* [bidi](https://github.com/juxt/bidi)
* [pushy](https://github.com/kibu-australia/pushy)
* [devtools](https://github.com/binaryage/cljs-devtools)
* redux time travelling (part of this project :-))

## Demo
[https://bamse.ln2.nl](https://bamse.ln2.nl)

## Development mode
`npm ci`

`npm run watch`

And after compile run:

`npm run dev`

#### App
[http://localhost:3000](http://localhost:3000)

#### Devcards
[http://localhost:3000/devcards.html](http://localhost:3000/devcards.html)

#### Shadow-Cljs dashboard
[http://localhost:9630](http://localhost:9630)

## Production mode
### building
`npm ci`

`npm run release`
### running
`cd resources`

`npm ci --only-production`

`PORT=4000 node server/server.js`

[http://localhost:4000](http://localhost:4000)

## License
The license is MIT
