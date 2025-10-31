package org.jetlinks.community.auth.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author caizz
 */
public interface IMenuSort {
    String getParentId();

    String getOwner();

    Long getSortIndex();

    /**
     * 获取list中对应owner最小的sortIndex
     *
     * @param list
     * @return
     */
    static <T extends IMenuSort> Map<String, Long> getSortIndexMap(List<T> list) {
        Map<String, Long> ownerMinSortIndexMap = new HashMap<>();
        for (IMenuSort menuView : list) {
            String owner = menuView.getOwner();
            long sortIndex = menuView.getSortIndex();
            if (owner != null) {
                ownerMinSortIndexMap.merge(owner, sortIndex, Math::min);
            }
        }
        return ownerMinSortIndexMap;
    }

    /**
     * 比较两个IMenuSort对象，用于排序（考虑owner组内最小sortIndex）
     * 1. 按owner组内最小sortIndex排序
     * 2. 按owner排序，确保相同owner的菜单在一起
     * 3. 按parentId排序，空值在前
     * 4. 按sortIndex排序
     *
     * @param o1                   第一个MenuView对象
     * @param o2                   第二个MenuView对象
     * @param ownerMinSortIndexMap owner与组内最小sortIndex的映射
     * @return 比较结果
     */
    static int compareMenuViewWithOwnerMinSortIndex(IMenuSort o1, IMenuSort o2, Map<String, Long> ownerMinSortIndexMap) {
        // 先按owner组内最小sortIndex排序
        String owner1 = o1.getOwner();
        String owner2 = o2.getOwner();
        if (owner1 != null && owner2 != null) {
            long minSortIndex1 = ownerMinSortIndexMap.getOrDefault(owner1, Long.MAX_VALUE);
            long minSortIndex2 = ownerMinSortIndexMap.getOrDefault(owner2, Long.MAX_VALUE);

            int minSortIndexComparison = Long.compare(minSortIndex1, minSortIndex2);
            if (minSortIndexComparison != 0) {
                return minSortIndexComparison;
            }

            // 再按owner排序
            int ownerComparison = owner1.compareTo(owner2);
            if (ownerComparison != 0) {
                return ownerComparison;
            }
        } else if (owner1 != null) {
            return -1;
        } else if (owner2 != null) {
            return 1;
        }

        // 再按parentId排序，空值在前
        String parentId1 = o1.getParentId();
        String parentId2 = o2.getParentId();
        if (parentId1 == null && parentId2 == null) {
            // 都为空，按sortIndex排序
            return Long.compare(o1.getSortIndex(), o2.getSortIndex());
        } else if (parentId1 == null) {
            // 第一个为空，排在前面
            return -1;
        } else if (parentId2 == null) {
            // 第二个为空，排在前面
            return 1;
        } else {
            // 都不为空，先按parentId排序，再按sortIndex排序
            int parentComparison = parentId1.compareTo(parentId2);
            if (parentComparison != 0) {
                return parentComparison;
            }
            return Long.compare(o1.getSortIndex(), o2.getSortIndex());
        }
    }
}
