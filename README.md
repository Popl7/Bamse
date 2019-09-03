# Bamse

## Universal Re-frame setup running on Node.js

I added a re-frame frontend and passed the state from the server to the frontend.
Work in progress :-)

## Used libraries and features

* [clojurescript](https://clojurescript.org/)
* [re-frame](https://github.com/Day8/re-frame)
* [shadow-cljs](https://github.com/thheller/shadow-cljs)
* [bidi](https://github.com/juxt/bidi)
* [pushy](https://github.com/kibu-australia/pushy)
* [devtools](https://github.com/binaryage/cljs-devtools)
* [redux time travelling](https://gitlab.com/StevenT/re-frame-redux)

## Demo

[https://bamse.ln2.nl](https://bamse.ln2.nl)

## Development mode

```sh
npm ci
```

### Leiningen

```sh
lein client
lein server
npm run dev
```

### Shadow-cljs

```sh
npm run watch
npm run server
npm run dev
```

### App

SPA:
[http://localhost:4200](http://localhost:4200)
SSR:
[http://localhost:3000](http://localhost:3000)

## Production mode

Probably broken....

### building

```sh
npm ci
```

```sh
npm run release
```

### running

```sh
cd resources
```

```sh
npm ci --only-production
```

```sh
PORT=4000 node server/server.js
```

[http://localhost:4000](http://localhost:4000)

## License

The license is [MIT](LICENCE)
