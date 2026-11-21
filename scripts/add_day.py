"""Validate a day's new content and merge it into feed/feed.json + concepts_index.txt.

usage: python3 scripts/add_day.py new_day.json
new_day.json = {"questions": [...], "lessons": [...]}   (same schema as feed.json)
Exits non-zero with a list of problems if anything is wrong - fix and run again.
"""
import json, re, sys, os
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
FEED, INDEX = os.path.join(ROOT, 'feed/feed.json'), os.path.join(ROOT, 'concepts_index.txt')
Q_REQ = ['id', 'lvl', 'kind', 'concept', 'domain', 'title', 'scenario', 'schema', 'data', 'output', 'task', 'hint',
         'solution', 'simple', 'remember', 'why', 'tip', 'steps']
L_REQ = ['id', 'concept', 'why', 'simple', 'remember', 'explain', 'example', 'gotcha', 'line']

def norm(s):
    return re.sub(r'\s+', ' ', s).strip().lower()

def check_q(q, errs):
    i = q.get('id', '?')
    for k in Q_REQ:
        if not q.get(k):
            errs.append(f'{i}: missing {k}')
    if q.get('lvl') not in ('E', 'M', 'A'):
        errs.append(f'{i}: lvl must be E, M or A')
    if q.get('kind') not in ('sql', 'plsql'):
        errs.append(f'{i}: kind must be sql or plsql')
    for t in q.get('data', []):
        for r in t.get('rows', []):
            if len(r) != len(t.get('cols', [])):
                errs.append(f"{i}: table {t.get('name')} row has {len(r)} values for {len(t.get('cols', []))} cols")
    out = q.get('output') or {}
    for r in out.get('rows', []):
        if len(r) != len(out.get('cols', [])):
            errs.append(f'{i}: output row width mismatch')
    lines = [norm(l) for l in q.get('solution', '').split('\n')]
    for n, s in enumerate(q.get('steps') or [], 1):
        if not s.get('say'):
            errs.append(f'{i}: step {n} missing say')
        if 'find' not in s:
            errs.append(f'{i}: step {n} needs "find" (text from the solution)'); continue
        a = next((k for k, l in enumerate(lines) if norm(s['find']) in l), None)
        if a is None:
            errs.append(f"{i}: step {n} find {s['find']!r} not in solution")
        elif s.get('to') and not any(norm(s['to']) in l for l in lines[a:]):
            errs.append(f"{i}: step {n} to {s['to']!r} not found at/after find")
    if q.get('trend'):
        for k in ('topic', 'hook', 'hashtags'):
            if not q['trend'].get(k):
                errs.append(f'{i}: trend.{k} missing')
        if len(q['trend'].get('hook', '').split()) > 25:
            errs.append(f'{i}: trend.hook too long (max 20 words)')
    if q.get('tech'):
        for k in ('pattern', 'company', 'source', 'url'):
            if not q['tech'].get(k):
                errs.append(f'{i}: tech.{k} missing')

def main():
    new = json.load(open(sys.argv[1]))
    feed = json.load(open(FEED))
    errs = []
    for q in new.get('questions', []):
        check_q(q, errs)
    for l in new.get('lessons', []):
        for k in L_REQ:
            if not l.get(k):
                errs.append(f"{l.get('id', '?')}: missing {k}")
    if errs:
        print('PROBLEMS:\n' + '\n'.join(errs)); sys.exit(1)
    ids = {q['id'] for q in new.get('questions', [])} | {l['id'] for l in new.get('lessons', [])}
    feed['questions'] = [q for q in feed['questions'] if q['id'] not in ids] + new.get('questions', [])
    feed['lessons'] = [l for l in feed['lessons'] if l['id'] not in ids] + new.get('lessons', [])
    json.dump(feed, open(FEED, 'w'), ensure_ascii=False, indent=1)
    rebuild_index(feed)
    print(f"OK: {len(new.get('questions', []))} questions, {len(new.get('lessons', []))} lessons merged")

def rebuild_index(feed=None):
    feed = feed or json.load(open(FEED))
    bank = json.load(open(os.path.join(ROOT, 'app/src/main/assets/bank.json')))
    rows = []
    for q in bank['questions'] + feed['questions']:
        t = ' TECH:' + q['tech']['pattern'] if q.get('tech') else ''
        if q.get('trend'):
            t += ' TREND:' + q['trend'].get('topic', '')
        rows.append(f"{q['id']}|{q.get('lvl')}|{q.get('kind')}|{q.get('concept')}|{q.get('domain', '')}{t}")
    for l in bank['lessons'] + feed['lessons']:
        rows.append(f"{l['id']}|lesson|{l.get('concept')}")
    open(INDEX, 'w').write('\n'.join(rows) + '\n')

if __name__ == '__main__':
    if sys.argv[1:] == ['--index']:
        rebuild_index(); print('index rebuilt')
    else:
        main()
