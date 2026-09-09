from django.urls import path , include
from rest_framework import routers
from . import views

router = routers.DefaultRouter()
router.register('people', views.PeopleViewSet,basename='people')

app_name = 'people'
urlpatterns = [
    path('', include(router.urls)),
]