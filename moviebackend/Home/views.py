from rest_framework import generics
from rest_framework.permissions import AllowAny

from catalog.serializers import GenreSerializer, CountrySerializer, LanguageSerializer
from content.models import Movie,Series
from catalog.models import Genre,Country,Language
from django.db.models import Count
from content.serializers import MovieSerializer, SeriesSerializer

class PopularMoviesView(generics.ListAPIView):
    permission_classes = [AllowAny]
    queryset = Movie.objects.all()
    queryset = queryset.annotate(
        view_count=Count('recently_watched')
    ).order_by('-view_count')

    serializer_class = MovieSerializer


class PopularSeriesView(generics.ListAPIView):
    permission_classes = [AllowAny]
    queryset = Series.objects.all()
    queryset = queryset.annotate(
        view_count=Count('recently_watched')
    ).order_by('-view_count')

    serializer_class = SeriesSerializer


class TopRatedMoviesView(generics.ListAPIView):
    permission_classes = [AllowAny]
    queryset = Movie.objects.all()
    queryset = queryset.order_by('-imdb_rating')

    serializer_class = MovieSerializer


class TopRatedSeriesView(generics.ListAPIView):
    permission_classes = [AllowAny]
    queryset = Series.objects.all()
    queryset = queryset.order_by('-imdb_rating')
    serializer_class = SeriesSerializer


class LatestMoviesView(generics.ListAPIView):
    permission_classes = [AllowAny]
    queryset = Movie.objects.all()
    queryset = queryset.order_by('-release_date')
    serializer_class = MovieSerializer


class LatestSeriesView(generics.ListAPIView):
    permission_classes = [AllowAny]

    queryset = Series.objects.all()
    queryset = queryset.order_by('-release_date')
    serializer_class = SeriesSerializer


class GenresView(generics.ListAPIView):
    permission_classes = [AllowAny]

    queryset = Genre.objects.all()

    serializer_class = GenreSerializer


class CountriesView(generics.ListAPIView):
    permission_classes = [AllowAny]

    queryset = Country.objects.all()

    serializer_class = CountrySerializer

class LanguagesView(generics.ListAPIView):
    permission_classes = [AllowAny]

    queryset = Language.objects.all()

    serializer_class = LanguageSerializer

