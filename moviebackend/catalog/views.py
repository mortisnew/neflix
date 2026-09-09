from django.shortcuts import render
from rest_framework import viewsets
from .models import Language,Genre,Country,Quality
from .serializers import *
from permissions import OnlyAdmin


class LanguageViewSet(viewsets.ModelViewSet):
    permission_classes = (OnlyAdmin,)
    queryset = Language.objects.all()
    serializer_class = LanguageSerializer

class CountryViewSet(viewsets.ModelViewSet):
    permission_classes = (OnlyAdmin,)

    queryset = Country.objects.all()
    serializer_class = CountrySerializer

class GenreViewSet(viewsets.ModelViewSet):
    permission_classes = (OnlyAdmin,)

    queryset = Genre.objects.all()
    serializer_class = GenreSerializer

class QualityViewSet(viewsets.ModelViewSet):
    permission_classes = (OnlyAdmin,)

    queryset = Quality.objects.all()
    serializer_class = QualitySerializer

