def can_build(plat):
	return plat=="android"

def configure(env):
	if (env['platform'] == 'android'):
		env.android_add_java_dir("android/src")
		env.android_add_maven_repository("url 'https://adcolony.bintray.com/AdColony'")
		env.android_add_to_manifest("android/AndroidManifestChunk.xml")
		env.android_add_to_permissions("android/AndroidManifestPermissionsChunk.xml")
		env.android_add_dependency("compile 'de.greenrobot:eventbus:2.4.0'")
		env.android_add_dependency("compile 'com.google.android.gms:play-services-ads:10.2.1'")
		env.android_add_dependency("compile 'com.google.android.gms:play-services-location:10.2.1'")
		#env.android_add_dependency("compile 'com.squareup.picasso:picasso:2.5.2'")
		env.android_add_dependency("compile project(':mmedia')")
		env.android_add_dependency("compile project(':adcolony')")
		env.android_add_dependency("compile fileTree(dir: '../../../modules/appodeal/android/libs', include: '*.jar')")
