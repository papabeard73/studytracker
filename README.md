# 目標・学習管理アプリ（Springboot版）
 [![CI](https://github.com/papabeard73/studytracker/actions/workflows/ci.yml/badge.svg)](https://github.com/papabeard73/studytracker/actions/workflows/ci.yml)

## セットアップ手順（最低限）

1. 前提条件
   - Java 21
   - Git
   - （本番相当で確認する場合）PostgreSQL
2. リポジトリを取得
   - `git clone <this-repo>`
   - `cd studytracker`
3. テスト実行
   - `./gradlew test`
4. 開発起動（`dev`）
   - `./gradlew bootRun`
5. 本番相当起動（`prod`）
   - `SPRING_PROFILES_ACTIVE=prod DB_URL=... DB_USERNAME=... DB_PASSWORD=... ./gradlew bootRun`

## 環境変数一覧

| 変数名                   | 必須           | 用途                                 | 例                               |
| ------------------------ | -------------- | ------------------------------------ | -------------------------------- |
| `SPRING_PROFILES_ACTIVE` | prod時のみ必須 | 実行プロファイル切替                 | `prod`                           |
| `DB_URL`                 | prod時のみ必須 | PostgreSQL接続URL                    | `jdbc:postgresql://host:5432/db` |
| `DB_USERNAME`            | prod時のみ必須 | DBユーザー名                         | `studytracker_user`              |
| `DB_PASSWORD`            | prod時のみ必須 | DBパスワード                         | `********`                       |
| `APP_ERROR_SHOW_DETAIL`  | 任意           | エラー詳細表示切替（通常は設定不要） | `false`                          |

## 実行方法（Profiles）

- デフォルトプロファイルは `dev`（`application.yml` で `spring.profiles.default: dev`）
- `DataLoader` は `@Profile("dev")` のため、初期データ投入は開発環境のみ実行されます

### 開発環境（dev）

```bash
./gradlew bootRun
```

- DB: H2（in-memory）
- H2 Console: `http://localhost:8080/h2-console`

### 本番相当（prod）

```bash
SPRING_PROFILES_ACTIVE=prod \
DB_URL=jdbc:postgresql://<host>:5432/<db> \
DB_USERNAME=<username> \
DB_PASSWORD=<password> \
./gradlew bootRun
```

- DB: PostgreSQL
- JPA: `ddl-auto=validate`
- Flyway: 起動時に `src/main/resources/db/migration` の未適用SQLを自動実行

## 1. 要件定義
### 1-1. 目的の整理
  - 用途：Discordを使っている同好会向け
  - 主機能：Discord内で宣言された目標に対して、個々人が 学習内容・学習時間を記録でき、累積時間を確認したり、自分の取り組みを見える化できる。
  - 要件
    - 無料 or できるだけ安価で運用したい
    - 小規模から始めて拡張可能にしたい

### 1-2. 利用者
- 初期想定：Discord同好会メンバーが「個人利用（自分の記録のみ）」を行う
- 将来的に：ログイン or Discord連携によってマルチユーザー化も可能

### 1-3. 機能（現状）
| 状態       | 機能                 | 内容                                           |
| ---------- | -------------------- | ---------------------------------------------- |
| ✅ 実装済み | Goal CRUD            | 目標の作成・一覧・編集・削除                   |
| ✅ 実装済み | StudyRecord CRUD     | 学習記録の作成・一覧・編集・削除               |
| ✅ 実装済み | バリデーション       | `@NotBlank`, `@NotNull`, `@Positive` など      |
| ✅ 実装済み | サービス層分離       | `GoalService`, `StudyRecordService`            |
| ✅ 実装済み | トランザクション     | 更新/削除系に `@Transactional` を付与          |
| ✅ 実装済み | スキーマ管理         | Flyway (`db/migration/V1__init.sql`)           |
| ✅ 実装済み | 例外ハンドリング     | `@ControllerAdvice` + `error/400/404/500`      |
| ✅ 実装済み | dev/prod分離         | `application-dev.yml` / `application-prod.yml` |
| ⏳ 未実装   | REST API公開         | 現在はThymeleaf中心（MVC）                     |
| ⏳ 未実装   | DTO/Mapper導入       | 現時点はEntityを直接利用                       |
| ⏳ 未実装   | Scheduler/集計自動化 | 将来拡張予定                                   |

## 2. 非機能要件
- セキュリティ：CSRF対策、入力バリデーション、簡易的な管理者認証（将来的にSpring Security導入を検討）
- ログ：アクセスログ・例外ログを出力（SLF4J + Logback）
- 保守性：Controller・Service・Repository層の責務を明確化
- 環境：ローカル(H2) ⇄ 本番(PostgreSQL)のプロファイル分離

## 3. 画面設計（現状仕様）
### 3-1. 共通UI
- ヘッダー/フッターを `fragments` で共通化

### 3-2. 目標一覧画面（`/goals`）
- ステータス別（`NOT_STARTED` / `ACTIVE` / `COMPLETED`）に目標を表示
- 各目標で「詳細を見る」へ遷移
- `ACTIVE` の目標には「記録を追加」導線を表示

### 3-3. 目標フォーム（`/goals/new`, `/goals/{id}/edit`）
- 入力項目: タイトル、目標日、説明、ステータス
- バリデーションエラー時は同画面にエラー表示

### 3-4. 目標詳細画面（`/goals/{id}`）
- 目標情報表示
- 紐づく学習記録一覧（編集/削除）
- 合計学習時間を `X時間 Y分` 形式で表示

### 3-5. 学習記録フォーム（`/goals/{goalId}/records/new`, `/edit`）
- 入力項目: 日付、内容、学習時間（分）
- バリデーションエラー時は同画面にエラー表示

## 4. 画面遷移（現状）
```text
[目標一覧] -> [目標追加]
[目標一覧] -> [目標詳細]
[目標詳細] -> [目標編集]
[目標詳細] -> [学習記録追加/編集]
```

## 5. データ構造（現状）
- Goal
  - id, user_id, title, target_date, description, status(enum), created_at, updated_at
- StudyRecord
  - id, goal_id(FK), recorded_at, content, duration_minutes

### 5-1. 将来拡張案
- User導入（認証・マルチユーザー化）
- Discord Bot連携
- CSV出力
- カレンダー表示/統計グラフ

## 6. 構成・環境構成
  ### 6-1. 技術構成
  | 項目              | Spring Boot版での対応                        |
  | ----------------- | -------------------------------------------- |
  | 言語              | Java                                         |
  | Webフレームワーク | Spring Boot (Spring MVC, Thymeleaf)          |
  | データアクセス    | Spring Data JPA + Hibernate                  |
  | DB                | H2（dev）, PostgreSQL（prod）                |
  | テンプレート      | Thymeleaf（HTML5対応のテンプレートエンジン） |
  | 構成管理          | Maven or Gradle（どちらでもOK、今はGradle）  |
  | デプロイ          | Render（Spring Bootも対応）                  |

  ### 6-2. 実行環境
  | 環境         | 内容                                |
  | ------------ | ----------------------------------- |
  | 開発環境     | macOS (M1), VSCode or IntelliJ      |
  | 実行環境     | Render（無料プラン）                |
  | DB           | H2（開発用）, PostgreSQL（本番）    |
  | ビルドツール | Gradle                              |
  | テンプレート | Thymeleaf                           |
  | フロント     | Tailwind CSS（CDN）＋簡易JavaScript |


## 7. 現在の構成ディレクトリ（主要）
```
studytracker/
├── src/
│   ├── main/
│   │   ├── java/com/example/studytracker/
│   │   │   ├── StudytrackerApplication.java
│   │   │   ├── config/
│   │   │   │   └── DataLoader.java
│   │   │   ├── controller/
│   │   │   │   ├── GoalController.java
│   │   │   │   └── HelloController.java
│   │   │   ├── service/
│   │   │   │   ├── GoalService.java
│   │   │   │   └── StudyRecordService.java
│   │   │   ├── repository/
│   │   │   │   ├── GoalRepository.java
│   │   │   │   └── StudyRecordRepository.java
│   │   │   ├── entity/
│   │   │       ├── Goal.java
│   │   │       ├── GoalStatus.java
│   │   │       └── StudyRecord.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── ResourceNotFoundException.java
│   │   └── resources/
│   │       ├── db/migration/
│   │       │   └── V1__init.sql
│   │       ├── templates/
│   │       │   ├── goals/
│   │       │   ├── fragments/
│   │       │   └── error/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   └── images/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
│       └── java/com/example/studytracker/
│           ├── controller/
│           ├── service/
│           └── repository/
└── build.gradle
```

## 8. Renderへのデプロイについて
- Render（Spring Boot、無料枠）
  - RenderのJava環境で以下のように設定する：
    - Build Command：`./gradlew build`
    - Start Command：java -jar build/libs/studytracker-0.0.1-SNAPSHOT.jar
    - Environment Variables：
      - `SPRING_PROFILES_ACTIVE=prod`
      - `DB_URL=jdbc:postgresql://...`
      - `DB_USERNAME=...`
      - `DB_PASSWORD=...`
  - デプロイ時の流れ
    - `./gradlew build` でJarを作成
    - アプリ起動時にFlywayが `db/migration` を自動適用
    - その後、アプリ本体が起動（`ddl-auto=validate`）
  - 既存DBへ初回導入する場合
    - 既存スキーマと `V1__init.sql` の差分を確認してから適用（必要に応じてbaseline戦略を採用）

## デプロイチェックリスト

- [ ] `SPRING_PROFILES_ACTIVE=prod` で起動している（`dev` のままではない）
- [ ] 本番DB接続情報（`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`）が設定済み
- [ ] `DataLoader` が本番で実行されないことを確認（`@Profile("dev")`）
- [ ] `./gradlew test` が成功している
- [ ] `./gradlew build` が成功し、Jarが生成されている
- [ ] 本番で `ddl-auto=validate` で起動確認できている
- [ ] 主要画面（一覧、詳細、追加、編集、削除）を手動確認済み
- [ ] エラーページ（404/500）とログ出力を確認済み

## 9. 開発ステップ（予定）
1. 最小構成で起動確認（Hello Spring Boot!）
2. Goalエンティティ・リポジトリ作成 → CRUD実装
3. StudyRecordエンティティ追加・Goalと関連付け
4. Thymeleafで一覧・登録画面を作成
5. 累計時間の集計ロジック追加
6. H2 → PostgreSQLへ移行
7. Renderへデプロイ
8. （余裕があれば）Scheduler機能・Chart.jsによる可視化を追加

## 10. 公開について
本アプリケーションは、Spring BootおよびJPAの学習目的・ポートフォリオとして作成中です。

## 11. ポートフォリオ
- 認証、DB設計、エラーハンドリング、テスト、CICD

## 12. CI / CD

本プロジェクトでは GitHub Actions を用いて CI を構築しています。

- push / pull request 時に自動実行
- 実行内容
  - 単体テスト（Service層中心）
  - ビルドチェック（Gradle）

これにより、機能追加やリファクタリング時の品質を担保しています。

## 13. Logging

本アプリケーションでは SLF4J + Logback によるログ出力を行っています。

- 業務イベント（作成・更新・削除）を INFO ログとして出力
- 例外発生時は ControllerAdvice で ERROR ログを出力
- 本番環境では Render のログ機能で確認可能

## 14. エラーハンドリング方針（400/404/500）

- 400 Bad Request:
  - 入力値不正（`BindException` / `ConstraintViolationException` / `IllegalArgumentException`）は `error/400` を返す
- 404 Not Found:
  - 業務データ未検出（`ResourceNotFoundException`）とURL未検出（`NoResourceFoundException`）を `error/404` に統一
- 500 Internal Server Error:
  - 想定外例外は `error/500` を返す
  - エラー詳細（`ex.getMessage()`）は `app.error.show-detail` で制御（`dev: true`, `prod: false`）
