/*
* Copyright 2021 Hugo Visser
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package nl.littlerobots.vcu.plugin

import nl.littlerobots.vcu.model.VersionCatalog

internal fun VersionCatalog.withKeepUnusedVersions(currentCatalog: VersionCatalog, keep: Boolean): VersionCatalog {
    if (!keep) {
        return this
    }
    return copy(versions = currentCatalog.versions + versions)
}

internal fun VersionCatalog.withKeptVersions(
    currentCatalog: VersionCatalog,
    refs: Set<VersionCatalogRef>
): VersionCatalog {
    val keptVersions = refs.filterIsInstance<VersionRef>().filter {
        currentCatalog.versions.containsKey(it.versionName) && !versions.containsKey(it.versionName)
    }.mapNotNull { ref ->
        currentCatalog.versions[ref.versionName]?.let {
            ref.versionName to it
        }
    }.toMap()
    return copy(versions = currentCatalog.versions.updateKeepingOrdering(versions, keptVersions))
}

private fun <K, V> Map<K, V>.updateKeepingOrdering(updates: Map<K, V>, keptEntries: Map<K, V>): Map<K, V> {
    return mapNotNull { entry ->
        (updates[entry.key] ?: keptEntries[entry.key])?.let {
            entry.key to it
        }
    }.toMap() + updates
}
