/*
 * Copyright 2006-2009 Odysseus Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package de.odysseus.el.tree;

import de.odysseus.el.TestCase;
import de.odysseus.el.tree.Tree;
import de.odysseus.el.tree.TreeStore;
import de.odysseus.el.tree.impl.Cache;

public class TreeStoreTest extends TestCase {
	public void test() {
		TreeStore store = new TreeStore(BUILDER, new Cache(1));
		assertSame(BUILDER, store.getBuilder());

		Tree tree = store.get("1");
		assertNotNull(tree);
		assertSame(tree, store.get("1"));
	}
}
