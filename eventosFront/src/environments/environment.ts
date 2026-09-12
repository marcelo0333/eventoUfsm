// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
//
// IMPORTANTE (teste em dispositivo físico):
// No celular, "localhost" aponta para o próprio aparelho — por isso usamos o IP
// da máquina de desenvolvimento na rede local (LAN). Troque o IP abaixo se o da
// sua máquina mudar (veja com `ipconfig`). O celular precisa estar na MESMA rede
// Wi-Fi/LAN do PC, e o backend precisa estar rodando (ele já escuta em 0.0.0.0).
const DEV_HOST = '192.168.0.102';

export const environment = {
  production: false,
  api: `http://${DEV_HOST}:9090/api`,
  recommendationApi: `http://${DEV_HOST}:8001/recommendations`,
  userKey: 'user_key',
  tokenKey: 'token_key'
};
/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
