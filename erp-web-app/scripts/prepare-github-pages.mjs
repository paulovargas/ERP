import { cp, mkdir, rm, copyFile, writeFile } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const currentDir = dirname(fileURLToPath(import.meta.url));
const appRoot = resolve(currentDir, '..');
const repoRoot = resolve(appRoot, '..');
const buildDir = resolve(appRoot, 'dist', 'erp-web-app', 'browser');
const docsDir = resolve(repoRoot, 'docs');

await rm(docsDir, { recursive: true, force: true });
await mkdir(docsDir, { recursive: true });
await cp(buildDir, docsDir, { recursive: true });
await copyFile(resolve(docsDir, 'index.html'), resolve(docsDir, '404.html'));
await writeFile(resolve(docsDir, '.nojekyll'), '');
