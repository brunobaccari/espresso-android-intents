from pathlib import Path
import os
import shutil
import subprocess
from dotenv import load_dotenv

ROOT = Path(__file__).resolve().parent
SOURCE = ROOT / '.upstream'
SAMPLE = 'ui/espresso/IntentsBasicSample'

if __name__ == '__main__':
    load_dotenv(ROOT / '.env')
    if not SOURCE.exists():
        subprocess.run(['git', 'clone', '--filter=blob:none', '--no-checkout',
                        os.environ['APP_SOURCE_URL'], str(SOURCE)], check=True)
    subprocess.run(['git', '-C', str(SOURCE), 'sparse-checkout', 'set', SAMPLE], check=True)
    subprocess.run(['git', '-C', str(SOURCE), 'checkout', '--detach', os.environ['APP_SOURCE_REF']], check=True)
    actual = subprocess.check_output(['git', '-C', str(SOURCE), 'rev-parse', 'HEAD'], text=True).strip()
    if actual != os.environ['APP_SOURCE_REF']:
        raise SystemExit('Unexpected application revision')
    app = SOURCE / SAMPLE / 'app'
    destination = app / 'src/portfolioTest/java/com/example/android/testing/espresso/IntentsBasicSample'
    destination.mkdir(parents=True, exist_ok=True)
    shutil.copy2(ROOT / 'src/androidTest/java/ContactIntentsTest.java', destination)
    build = app / 'build.gradle'
    original = build.read_text(encoding='utf-8')
    config = "\nandroid.sourceSets.androidTest.java.setSrcDirs(['src/portfolioTest/java'])\n"
    if config not in original:
        build.write_text(original + config, encoding='utf-8')
    print(f'IntentsBasicSample {actual}; portfolio tests injected, app logic unchanged.')
