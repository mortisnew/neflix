from django.urls import path,include
from rest_framework import routers
from . import views

router = routers.DefaultRouter()
router.register('languages', views.LanguageViewSet, basename='language')
router.register('countries', views.CountryViewSet, basename='country')
router.register('genres', views.GenreViewSet, basename='genre')
router.register('qualities', views.QualityViewSet, basename='quality')
urlpatterns = [
    path('',include(router.urls)),
]