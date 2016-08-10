package com.wondersgroup.healthcloud.helper.family;


import com.google.common.base.Preconditions;

public class FamilyMemberAccess {

    private static final int mask = 0b111;

    private static final int readAccess = 0b100;//读取权限
    private static final int writeAccess = 0b010;//修改权限
    private static final int xAccess = 0b001;//因为没有可执行这个权限的用途, 所以作为保留字段

    private static final int size = 3;
    private static final int recordAccessOffset = 0 * size;

    public static class Builder {
        private int value;

        public Builder() {
        }

        public Builder(String value) {
            this.value = Integer.valueOf(value);
        }

        public Builder recordAccess(Boolean readable, Boolean writeable) {
            if (readable) {
                value |= (readAccess << recordAccessOffset);
            } else {
                value = value & ~(readAccess << recordAccessOffset);
            }
            if (writeable) {
                value |= (writeAccess << recordAccessOffset);
            } else {
                value = value & ~(writeAccess << recordAccessOffset);
            }
            return this;
        }

        public String build() {
            return String.valueOf(value);
        }
    }

    public static Boolean recordReadable(String access) {
        Integer value = Integer.valueOf(access);
        return getReadable(value, 0);
    }

    private static int getOneGroupAccess(int access, int offset) {
        int newMask = mask << (offset * size);
        return (access & newMask) >> (offset * size);
    }

    private static Boolean getReadable(int groupAccess) {
        Preconditions.checkArgument(-1 < groupAccess && groupAccess < 8);
        return readAccess == (groupAccess & readAccess);
    }

    private static Boolean getReadable(int access, int offset) {
        return readAccess == ((access >> (offset * size)) & readAccess);
    }

    private static Boolean getWritable(int groupAccess) {
        Preconditions.checkArgument(-1 < groupAccess && groupAccess < 8);
        return writeAccess == (groupAccess & writeAccess);
    }

    private static Boolean getWritable(int access, int offset) {
        return writeAccess == ((access >> (offset * size)) & writeAccess);
    }
}
