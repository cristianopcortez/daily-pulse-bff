# daily-pulse-bff

BFF GraphQL em Ktor para o DailyPulse. Agrega fontes de notícia (NewsAPI hoje), normaliza os dados e devolve só o contrato que o app usa nas telas.

Foi projetado para ser consumido pelo client Kotlin Multiplatform [DailyPulse-native-mpp](https://github.com/cristianopcortez/DailyPulse-native-mpp). O app não chama a NewsAPI direto: a API key fica só neste BFF (`NEWS_API_KEY`).

## O que entra e o que não entra

O client tem duas telas remotas: **Articles** e **Sources**. A tela **About** é informação local do device e **não** passa por este BFF.

Cache (SQLDelight) e pull-to-refresh ficam no app. Refresh é a mesma query GraphQL de novo (`forceFetch` no cliente). Este serviço não usa banco no v1.

## Contrato GraphQL

Campos da UI são non-null. Null da NewsAPI vira fallback no BFF.

### Articles

Cada card: `title`, `desc`, `date`, `imageUrl`.

- `date` — ISO-8601 UTC (`2024-01-15T12:00:00Z`). O KMP formata "Today" / "Yesterday" / "N days ago".
- `desc` vazio/null → `"Click to find out more"`
- `imageUrl` vazio/inválido → imagem fallback CNBC

Argumento `source: String` (opcional): id da fonte (ex. `bbc-news`). Sem `source`, o BFF usa o comportamento atual do app: `country=us`, `category=business`.

Não expõe author, content, url, objeto source da NewsAPI, etc.

```graphql
query Articles($source: String) {
  articles(source: $source) {
    title
    desc
    date
    imageUrl
  }
}
```

### Sources

Cada card: `id`, `name`, `desc`, `origin`.

- `id` — chave para filtrar articles (não aparece na UI)
- `origin` — texto pronto para a UI, `{country} - {language}` (ex. `us - en`)

Não expõe url nem category no v1.

```graphql
query Sources {
  sources {
    id
    name
    desc
    origin
  }
}
```

### Erros

Falhas da fonte (quota, 401, timeout) voltam como GraphQL errors com mensagem segura, sem vazar a API key.

## Rodar local

Requisitos: JDK 21, Gradle Wrapper do repo, chave em [newsapi.org](https://newsapi.org/).

```bash
cp .env.example .env
# preencha NEWS_API_KEY
./gradlew run
```

| Endpoint | Uso |
| --- | --- |
| `POST http://localhost:8080/graphql` | Queries do app |
| `http://localhost:8080/graphiql` | IDE GraphQL (só com `io.ktor.development=true`, ativo no `./gradlew run`) |
| `GET http://localhost:8080/health` | Health check |

```bash
./gradlew test
```

## Cloud Run

O servidor escuta `0.0.0.0` e a porta `PORT` (padrão 8080). Injete `NEWS_API_KEY` como secret. GraphiQL não sobe no fat jar de produção.

Build da imagem: `Dockerfile` na raiz (`buildFatJar`).

## Arquitetura

- Schema GraphQL gerado a partir dos tipos Kotlin (`Article`, `Source`, `Query.articles`, `Query.sources`)
- `NewsProvider` — contrato estável para o app; um segundo adapter entra atrás dos mesmos tipos
- `NewsApiProvider` / `NewsApiClient` — primeira fonte (REST NewsAPI, key no header `X-Api-Key`)
