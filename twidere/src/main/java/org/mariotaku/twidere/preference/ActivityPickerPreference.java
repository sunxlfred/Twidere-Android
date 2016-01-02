/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.preference;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;

import java.util.List;

public abstract class ActivityPickerPreference extends ComponentPickerPreference {

    public ActivityPickerPreference(final Context context) {
        this(context, null);
    }

    public ActivityPickerPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected final ComponentName getComponentName(ResolveInfo info) {
        return new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
    }

    @Override
    protected List<ResolveInfo> resolve(Intent queryIntent) {
        return packageManager.queryIntentActivities(queryIntent, 0);
    }
}