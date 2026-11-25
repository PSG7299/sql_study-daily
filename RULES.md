# Daily question rules (read by the 06:17 IST scheduled task)

Learner: Oracle PL/SQL developer, 5+ years (data migration, reconciliation, utPLSQL, payroll packages),
switching jobs (banking/finance and product companies) in India. NO hands-on performance-tuning experience; confidence is
fragile, so easy questions must be genuinely easy. Each question also becomes a YouTube Short
(Query with Berry), so keep spoken parts short.

## Daily mix: 8 questions + 1 lesson
- 2 × `"lvl":"E"`: one basic concept each (GROUP BY, HAVING, JOIN types, CASE, NVL/COALESCE, DISTINCT,
  ORDER BY/FETCH FIRST, IN/BETWEEN/LIKE, simple subquery, basic date functions, UNION). Solution 3-6 lines.
- 2 × `"lvl":"M"`: realistic, one twist (NULL, tie, date boundary, LEFT JOIN, ROW_NUMBER/RANK, LAG, simple PIVOT).
- 4 × `"lvl":"A"`: window frames, gaps and islands, MATCH_RECOGNIZE, recursive WITH, ROLLUP/CUBE/GROUPING SETS,
  MERGE, bulk PL/SQL, collections, dynamic SQL with binds, autonomous transactions, locking, compound triggers,
  partitioning, materialized views. At least 2 have `"kind":"plsql"` (one becomes the weekday Short, the other goes into the Sunday long video).
- Exactly 1 of the 8 (usually an A sql) is a **tech-interview** question: an ORIGINAL question built on a
  pattern known to be asked at tech/fintech companies (DataLemur / LeetCode SQL lists), e.g. second-highest
  salary, Nth transaction per user, repeated payments within N minutes, consecutive years/days (islands),
  rolling averages, top-N per group, retention / reactivated users, YoY growth, median, swapped rows,
  manager hierarchy, histogram of counts, card launch success. Give it a banking twist where natural.
  NEVER copy their question text, tables or data. Add
  `"tech": {"pattern": "<pattern name>", "company": "<company it is listed for>", "source": "DataLemur" or "LeetCode", "url": "https://datalemur.com/questions" or "https://leetcode.com/studyplan/top-sql-50/"}`.
  Rotate patterns; check concepts_index.txt (`TECH:` entries) to avoid repeats.
- Exactly 2 of the 8 are **trending** questions, for YouTube views (never the tech one):
  1. **Trending (non-movie)** (an E or M `sql`): NO movies, films, box office, OTT film releases or actors.
     Search wider and rotate between: cricket and other sports (series, IPL auction, World Cup, kabaddi, F1),
     phone and gadget launches, AI and tech news (new AI apps, app downloads), big online sale events
     (Black Friday, year-end sales), travel and festive/wedding season, food-delivery and quick-commerce trends,
     EV and auto launches, startup IPO listings, video-game launches, viral apps, weather-driven demand
     (winter wear, air purifiers; never disasters), government digital services (UPI, FASTag, DigiLocker
     stats). Use the topic's own data: players and scores, orders, downloads, bookings, sales by city.
  2. **Markets** (any level): what is moving this week in gold or silver, crude oil, USD/INR and other forex,
     Sensex/Nifty, big IPO listings, RBI rate decisions. Build it around bank work: gold-loan LTV checks,
     forex desk conversions, daily % change, streaks of falls, moving averages, treasury exposure.
  First run 2-3 web searches for what is trending in India this week (one for sports/tech/shopping news, one for markets).
  Rules: NO politics, elections, wars, disasters, deaths,
  crime news, movies or celebrities' private lives. Keep any religious or festival topic respectful. ALL numbers are invented sample data: never present them as real figures or
  real prices, never predict prices or give investment advice, and do not use real people as characters.
  Do not repeat a topic used in the last 5 days (check `TREND:` entries in concepts_index.txt). Add
  `"trend": {"topic": "<short searchable name, e.g. GTA 6 or Gold Price Fall>", "hook": "<one spoken sentence,
  max 20 words>", "hashtags": ["gta6", "gtavi"]}`.
- **Domain mix (every day): at most 3 of the 8 in banking/finance** (core banking, loans, cards, UPI, KYC, trading,
  treasury, insurance; the markets trending question counts as one of these). The other 5+ come from DIFFERENT
  everyday domains, one domain per question and no domain twice in a day, rotating through: e-commerce
  (Flipkart/Amazon-style orders, returns, carts), food delivery (Swiggy/Zomato-style orders, riders, ratings),
  ride-hailing and logistics (trips, drivers, deliveries, warehouses), streaming and social media (watch time,
  subscriptions, followers, posts), sports and IPL (matches, players, runs, auctions), airlines and travel
  (bookings, delays, seats), telecom (recharges, data usage, churn), healthcare (appointments, doctors,
  pharmacy stock), education and ed-tech (students, courses, exams), HR and payroll (employees, salaries,
  managers, attendance), retail and supermarkets (inventory, billing), gaming (players, matches, in-app
  purchases), real estate, hotels, electricity and utilities. Check concepts_index.txt domains and prefer the
  ones used least in the last 7 days. Scenarios stay Indian (cities, rupees, names). The pop-culture trending
  question can use its own natural domain (box office, gaming, sports) rather than a banking angle.
  Prefer concepts not in concepts_index.txt.

## Question fields (exactly)
id (`f` + YYYYMMDD + `-` + n), lvl, kind (`sql`/`plsql`), concept (2-5 words), domain, title (max 7 words),
scenario (2-3 SHORT sentences, max 55 words), schema (table defs with types and short comments, `\n` breaks),
data, output, output_note (optional, max 2 sentences), task (1-2 sentences, max 45 words), hint (1 sentence),
solution (correct Oracle 19c), simple, remember, why (2-3 technical sentences), tip (1 interview sentence),
steps, tech (only for the tech question), trend (only for the 2 trending questions).

- **data** (required) covers EVERY table the solution reads: `{"name", "cols" (schema order), "rows" (strings)}`,
  3-7 rows each (an output/log table may have 0 rows). `"NULL"` for nulls, dates `DD-MM-YYYY` or
  `DD-MM-YYYY HH24:MI`. Rows must make the twist visible and the answer non-empty. Avoid SYSDATE; use a fixed
  date in the task, or output_note "If today is DD-MM-YYYY".
- **output** (required) `{"cols", "rows"}`: exactly what the solution returns, formatted like data.
  COMPUTE IT: `pip install --break-system-packages duckdb`, load the data with proper types, run a faithful
  DuckDB translation (ADD_MONTHS → + to_months(n), NVL → COALESCE, date - date → datediff, CONNECT BY →
  WITH RECURSIVE, MINUS → EXCEPT, PIVOT → COUNT(*) FILTER) and copy the result. DML: show the changed table and
  say so in output_note. PL/SQL: trace by hand, show the resulting table state or error, explain in output_note.
- **simple** (most important): a 10-year-old must understand. 3-4 short sentences, start with an everyday
  comparison (passbook, school register, piggy bank, stopwatch, queue, race with a tie), then what the SQL does.
  No unexplained jargon. **remember**: one hook under 12 words ("LAG = peek at the row before me.").
- **steps** (for the video): 3-5 items `{"say": "...", "find": "<exact text from one solution line>",
  "to": "<text from a later line, optional>"}`. `say` is spoken by Berry: max 25 words, plain spoken English,
  start with "Step one." / "Step two." / "Finally,". Lines from `find` to `to` are highlighted while it is said.
  Copy find/to exactly from the solution (case and spacing do not matter).

## Lesson fields (exactly)
id (`fl` + YYYYMMDD), concept, why (1 sentence), simple, remember, explain (3-4 technical sentences),
example (short runnable Oracle example using WITH ... FROM dual), gotcha (1 sentence), line (1 interview sentence).

## Saving
Put the day's content in `new_day.json` as `{"questions": [...], "lessons": [...]}` and run
`python3 scripts/add_day.py new_day.json`. It validates everything (including steps find/to) and merges into
feed/feed.json and concepts_index.txt. Fix every reported problem and re-run until it prints OK.
Do not read feed/feed.json or bank.json in full: concepts_index.txt lists every existing id and concept.
