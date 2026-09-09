from builtins import classmethod, super

from rest_framework import serializers
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer

from .models import ModelUser

class RegistrationSerializer(serializers.ModelSerializer):
    password2 = serializers.CharField(write_only=True)
    class Meta:
        model = ModelUser
        fields = ('username', 'email', 'password','password2')
        extra_kwargs = {'password':
                            {'write_only': True}}
    def validate(self, data):
        if data['username'] == 'admin':
            raise serializers.ValidationError({'username': 'Username cant be admin'})
        if data['password'] != data['password2']:
            raise serializers.ValidationError({'password': 'Password is not match'})
        if data['username'] == 'manager':
            raise serializers.ValidationError({'username': 'Username cant be manager'})
        return data

    def create(self, validated_data):
        validated_data.pop('password2')
        user = ModelUser(**validated_data)
        user.set_password(validated_data['password'])
        user.save()
        return user

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = ModelUser
        fields = ('id', 'username', 'email','is_staff', 'is_active', 'date_joined', 'last_login')

class LoginSerializer(serializers.Serializer):
    email = serializers.EmailField()
    password = serializers.CharField(write_only=True)


class MyTokenObtainPairSerializer(TokenObtainPairSerializer):
    @classmethod
    def get_token(cls, user):
        token = super().get_token(user)
        token['email'] = user.email

        return token