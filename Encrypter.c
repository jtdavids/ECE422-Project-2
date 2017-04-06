
#include <jni.h>
#include <stdio.h>

void encrypt (long *v, long *k){
/* TEA encryption algorithm */
unsigned long y = v[0], z=v[1], sum = 0;
unsigned long delta = 0x9e3779b9, n=32;

	while (n-- > 0){
		sum += delta;
		y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
	}

	v[0] = y;
	v[1] = z;
}

void decrypt (long *v, long *k){
/* TEA decryption routine */
unsigned long n=32, sum, y=v[0], z=v[1];
unsigned long delta=0x9e3779b9l;

	sum = delta<<5;
	while (n-- > 0){
		z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
		y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		sum -= delta;
	}
	v[0] = y;
	v[1] = z;
}

JNIEXPORT void JNICALL Java_Encrypter_encryptInC
  (JNIEnv *env, jobject jobj, jbyteArray val, jlongArray key){
  	//(long *v, long *k){
	/* TEA encryption algorithm */
	long* v = (long*)(*env)->GetByteArrayElements(env, val, NULL);
	long* k = (long*)(*env)->GetLongArrayElements(env, key, NULL);

	jsize data_length = (*env)->GetArrayLength(env, val)/8;

	for (int i = 0; i < data_length; i+=2)
	{
		encrypt(v+i, k);
	}
	/*
	unsigned long y = 0, z=0, sum = 0;
	unsigned long delta = 0x9e3779b9, n=32;

	printf("length of data: %d\n", data_length);
	int i = 0;
	for(i = 0; i<data_length; i=i+2){
		y = v[i];
		z = v[i+1];
		n = 32;

		while (n-- > 0){
			sum += delta;
			y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
			z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
		}

		v[i] = y;
		v[i+1] = z;
	}*/
	(*env)->ReleaseByteArrayElements(env, val, (jbyte*)v, 0);
	(*env)->ReleaseLongArrayElements(env, key, (jlong*)k, 0);
	return;
}
	

JNIEXPORT void JNICALL Java_Encrypter_decryptInC
  (JNIEnv *env, jobject obj, jbyteArray val, jlongArray key){
  	//(long *v, long *k){
	/* TEA decryption routine */
	long* v = (long*)(*env)->GetByteArrayElements(env, val, NULL);
	long* k = (long*)(*env)->GetLongArrayElements(env, key, NULL);

	jsize data_length = (*env)->GetArrayLength(env, val)/8;

	for (int i = 0; i < data_length; i+=2) {
		decrypt(v+i, k);
	}

	/*
	unsigned long y = 0, z=0, sum = 0;
	unsigned long delta = 0x9e3779b9, n=32;
	int i = 0;

	for(i = 0; i<data_length; i=i+2){
		y = v[i];
		z = v[i+1];
		n = 32;
		sum = delta<<5;

		while (n-- > 0){
			z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
			y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
			sum -= delta;
		}
		v[i] = y;
		v[i+1] = z;
	}*/
	(*env)->ReleaseByteArrayElements(env, val, (jbyte*)v, 0);
	(*env)->ReleaseLongArrayElements(env, key, (jlong*)k, 0);
	return;
}
