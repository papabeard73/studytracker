# 目標・学習管理アプリ（Springboot版）
 [![CI](https://github.com/papabeard73/studytracker/actions/workflows/ci.yml/badge.svg)](https://github.com/papabeard73/studytracker/actions/workflows/ci.yml)
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

### 1-3. 機能
| アイデア                               | 内容                                       | 実装のポイント                               |
| -------------------------------------- | ------------------------------------------ | -------------------------------------------- |
| ✅ **REST API化**                       | RESTで提供                                 | `@RestController` + JSONレスポンス           |
| ✅ **Thymeleafで簡易UI**                | HTMLテンプレートで一覧・登録フォームを作成 | `th:each`, `th:value` でバインディング       |
| ✅ **Validationの導入**                 | 入力チェック（例：学習時間は正の数）       | `@NotBlank`, `@Positive` などを付与          |
| ✅ **サービス層のロジック化**           | 「累計時間の計算」「日付別の集計」など     | `GoalService` や `StudyRecordService` に実装 |
| ✅ **H2 → PostgreSQL移行**              | ローカルはH2、本番（Render）はPostgreSQL   | `application-prod.yml` を別に用意            |
| ✅ **DTO・Mapper導入**                  | EntityとViewModelを分離                    | `MapStruct` などで自動変換も可能             |
| ✅ **スケジューラ（Spring Scheduler）** | 定期的に集計処理やリマインドメールを送信   | `@Scheduled` で実装                          |
| ✅ **グラフAPI提供（将来）**            | JSONで集計データを返す                     | 将来的にReactやDiscord Botと連携できる形     |

## 2. 非機能要件
- セキュリティ：CSRF対策、入力バリデーション、簡易的な管理者認証（将来的にSpring Security導入を検討）
- ログ：アクセスログ・例外ログを出力（SLF4J + Logback）
- 保守性：Controller・Service・Repository層の責務を明確化
- 環境：ローカル(H2) ⇄ 本番(PostgreSQL)のプロファイル分離

## 3. 画面設計（イメージ）
  ### 3-1. ヘッダー
  - GoalTrack、アイコン
  - Home、Discord、Discordアイコン
  ### 3-2. 目標一覧画面
  - 自分が登録した目標を一覧表示
    - ステータス：Not Started, Active Goals, Completed Goalsに分けた一覧表示
  - 各目標に対して：
    - 目標タイトル（テキスト）、達成目標日（日付）、累計学習時間（合計値）の表示
    - [学習記録を追加]、[詳細を見る] ボタン
  - 目標新規追加ボタン、累計を見るボタン

  ### 3-3. 目標追加フォーム
  - 目標タイトル（テキスト）、目標達成日（日付）、目標ステータス（プルダウン）、目標の説明（テキスト）を入力
  - [登録] 、[目標一覧へ戻る] ボタン

  ### 3-4. 目標詳細画面
  - 目標情報の表示
    - 目標タイトル（テキスト）、目標達成日（日付）、目標の説明（テキスト）
  - [目標の編集]ボタン、[学習記録を追加]ボタン
  - 紐づく学習記録の一覧表示
    - 各学習記録の [編集] [削除] ボタン
  - 累計学習時間（合計値）の表示

  ### 3-5. 目標編集フォーム
  - 既存の目標情報を編集
    - 目標タイトル（テキスト）、目標達成日（日付）、目標ステータス（プルダウン）、目標の説明（テキスト）を編集
  - [保存] [目標を削除] [一覧ページへ戻る] ボタン

  ### 3-6. 学習記録追加・編集フォーム
  - 日付、内容（テキスト）、学習時間（分単位）を入力
  - [保存] [目標詳細ページへ戻る] ボタン

  ### 3-7. 累計表示（※目標ごと or 全体）：後回し
  - 集計期間選択（今週、今月、任意期間）
  - 表示方法：棒グラフ、円グラフ
  - フィルター：目標別、ステータス別
  - 合計学習時間（全体）
  #### 補足：安全な削除の方法例
  - 削除前に確認ダイアログを出す
    - 👉「この目標を削除すると関連する学習記録も削除されます。本当によろしいですか？」
  - 「削除」ではなく「アーカイブ」にする方法もあり
    - 👉 間違って消しても復元できる設計に（後で検討）

## 4. 画面遷移
    ```
    [目標一覧]　→　[目標追加]
        ↓ クリック
    [目標詳細・学習記録一覧] → [目標編集]
        ↓
    [学習記録追加・編集]
    ```

## 5. データ構造（Entity案）
- User（将来用。初期はなし）
  - id, name, email, created_at
- Goal（目標）
  - → 学習目標を表すメインエンティティ。複数の学習記録（StudyRecord）を持つ。
  - id, title, description, user_id, status（未着手:Not started, 進行中:Active goals, 達成済:Completed goals）, target_date, created_at, updated_at
- StudyRecord（学習記録）
  - → 1回の学習セッションを表す。目標（Goal）に紐づく。
  - id, goal_id, content, duration_minutes(int:分単位), recorded_at
  ### 5-1.可能な拡張機能（将来的に）
  - Discord Bot連携して、メッセージを自動で取り込む（初期は手動でOK）
  - 学習記録のCSV出力
  - カレンダー表示や統計グラフ（学習ペースの可視化）

## 6. 構成・環境構成
  ### 6-1. 環境構成
  | 項目              | Spring Boot版での対応                        |
  | ----------------- | -------------------------------------------- |
  | 言語              | Java                                         |
  | Webフレームワーク | Spring Boot (Spring MVC, Thymeleaf)          |
  | データアクセス    | Spring Data JPA + Hibernate                  |
  | DB                | PostgreSQL                                   |
  | テンプレート      | Thymeleaf（HTML5対応のテンプレートエンジン） |
  | 構成管理          | Maven or Gradle（どちらでもOK、今はGradle）  |
  | デプロイ          | Render（Spring Bootも対応）                  |

  ### 6-2. 環境構成
  | 環境         | 内容                                |
  | ------------ | ----------------------------------- |
  | 開発環境     | macOS (M1), VSCode or IntelliJ      |
  | 実行環境     | Render（無料プラン）                |
  | DB           | H2（開発用）, PostgreSQL（本番）    |
  | ビルドツール | Gradle                              |
  | テンプレート | Thymeleaf                           |
  | フロント     | Tailwind CSS（CDN）＋簡易JavaScript |


## 7. 初期構成ディレクトリ
```
studytracker/
├── src/
│   ├── main/
│   │   ├── java/com/example/studytracker/
│   │   │   ├── StudytrackerApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── GoalController.java
│   │   │   │   └── StudyRecordController.java
│   │   │   ├── service/
│   │   │   │   ├── GoalService.java
│   │   │   │   └── StudyRecordService.java
│   │   │   ├── repository/
│   │   │   │   ├── GoalRepository.java
│   │   │   │   └── StudyRecordRepository.java
│   │   │   └── entity/
│   │   │       ├── Goal.java
│   │   │       └── StudyRecord.java
│   │   └── resources/
│   │       ├── templates/
│   │       │     └── goals/
│   │       │         ├── list.html
│   │       │         ├── detail.html
│   │       │         └── form.html
│   │       ├── static/
│   │       │   ├── css/
│   │       │   └── js/
│   │       └── application.yml
│   └── test/
│       └── ...
└── build.gradle
```

## 8. Renderへのデプロイについて
- Render（Spring Boot、無料枠）
  - RenderのJava環境で以下のように設定する：
    - Build Command：./gradlew build
    - Start Command：java -jar build/libs/studytracker-0.0.1-SNAPSHOT.jar
    - Environment Variables：DATABASE_URL, SPRING_PROFILES_ACTIVE=prod などを設定

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
- 認証、DB設計、エラーハンドリング、テスト、CICDが入っているもの

## 12. 後で追加するもの
- テスト実行コマンド
```
./gradlew test
```

- GitHub Actions のバッジを貼る
  - [![CI](https://github.com/papabeard73/studytracker/actions/workflows/ci.yml/badge.svg)](https://github.com/papabeard73/studytracker/actions/workflows/ci.yml)

## CI / CD

本プロジェクトでは GitHub Actions を用いて CI を構築しています。

- push / pull request 時に自動実行
- 実行内容
  - 単体テスト（Service層中心）
  - ビルドチェック（Gradle）

これにより、機能追加やリファクタリング時の品質を担保しています。

## Logging

本アプリケーションでは SLF4J + Logback によるログ出力を行っています。

- 業務イベント（作成・更新・削除）を INFO ログとして出力
- 例外発生時は ControllerAdvice で ERROR ログを出力
- 本番環境では Render のログ機能で確認可能
