from django.contrib.auth import authenticate
from django.template.context_processors import request
from rest_framework import permissions
from rest_framework.permissions import BasePermission

class OnlyAdmin(BasePermission):
    message = "Only Admin users can perform this action"
    def has_permission(self, request, view):
        if request.method in permissions.SAFE_METHODS:
            return True
        if request.user.is_staff or request.user.is_superuser:
            return True
        return False

class IsOwnerOrReadOnly(BasePermission):
    def has_object_permission(self, request, view, obj):
        if request.user == obj.user:
            return True
        if request.method in permissions.SAFE_METHODS:
            return True

        return False