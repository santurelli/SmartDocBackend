import os
import re

src_dir = r"c:\PROGETTI\smartdoc-backend\src\main\java"

for root, _, files in os.walk(src_dir):
    for file in files:
        if file.endswith(".java"):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            if "gnu.trove" not in content and "TLongArrayList" not in content and "TIntArrayList" not in content:
                continue

            # Replacements
            # Imports
            content = re.sub(r'import gnu\.trove\.list\.array\.TLongArrayList;', 'import java.util.ArrayList;\nimport java.util.List;', content)
            content = re.sub(r'import gnu\.trove\.list\.array\.TIntArrayList;', 'import java.util.ArrayList;\nimport java.util.List;', content)
            
            # Since some files might already import List/ArrayList, let's just make sure we don't duplicate them too much,
            # but Java compiler will just give a warning or ignore duplicate imports of the same class.
            
            # Types
            content = re.sub(r'\bTLongArrayList\b', 'List<Long>', content)
            content = re.sub(r'\bTIntArrayList\b', 'List<Integer>', content)
            
            # new Instances
            content = re.sub(r'new List<Long>\(\)', 'new ArrayList<>()', content)
            content = re.sub(r'new List<Integer>\(\)', 'new ArrayList<>()', content)

            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Refactored types in {file}")
