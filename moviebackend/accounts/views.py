from django.shortcuts import render
from rest_framework import viewsets
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from rest_framework import status
from rest_framework.views import APIView
from .models import ModelUser
from .serializers import RegistrationSerializer,LoginSerializer
from django.contrib.auth import authenticate, login, logout
from django.middleware.csrf import get_token

class RegisterAPIView(APIView):
    permission_classes = [AllowAny]
    def post(self, request):
        serializer = RegistrationSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class LoginAPIView(APIView):
    permission_classes = [AllowAny]
    def post(self, request):
        serializer = LoginSerializer(data=request.data)
        if serializer.is_valid():
            user = authenticate(username=serializer.validated_data['email'],
                                password=serializer.validated_data['password'])
            if user is not None:
                login(request, user)
                return Response({'status': 'success', 'message': 'Login successful'})
        return Response(
            {"detail": "Invalid username or password"},
            status=status.HTTP_401_UNAUTHORIZED,
        )

class LogoutAPIView(APIView):
    def get(self,request):
        logout(request)
        return Response({'status': 'success', 'message': 'Logout successful'})

class CSRFAPIView(APIView):
    permission_classes = [AllowAny]
    def get(self, request):
        token = get_token(request)

        return Response({
            "csrfToken": token
        })