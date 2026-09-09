from django.shortcuts import get_object_or_404
from rest_framework import viewsets, generics, filters, status
from rest_framework.exceptions import ValidationError
from rest_framework.generics import get_object_or_404
from rest_framework.response import Response
from content.models import Movie,Series
from content.serializers import SeriesSerializer, MovieSerializer
from permissions import OnlyAdmin
from .serializers import (FavoriteSerializer,
                          WatchlistSerializer, RecentlyWatchedSerializer, RecentlyEpisodeSerializer, FolderSerializer,
                          FolderItemSerializer, SearchSerializer)
from special.models import Favorite, Watchlist, RecentlyEpisode, RecentlyWatched, Folder, FolderItem
from rest_framework.permissions import IsAuthenticated
from django.utils import timezone

class FavoriteViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Favorite.objects.all()
    serializer_class = FavoriteSerializer

    def get_queryset(self):
        return Favorite.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

class WatchlistViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]

    queryset = Watchlist.objects.all()
    serializer_class = WatchlistSerializer

    def get_queryset(self):
        return Watchlist.objects.filter(user=self.request.user)
    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

class RecentlyEpisodeViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]

    queryset = RecentlyEpisode.objects.all()
    serializer_class = RecentlyEpisodeSerializer

    def get_queryset(self):
        return RecentlyEpisode.objects.filter(user=self.request.user)
    def perform_create(self, serializer):
        episode = serializer.validated_data['episode']
        recent = RecentlyEpisode.objects.filter(user=self.request.user, episode=episode)
        if recent.exists():
            recent.update(watched_at=timezone.now())
        else:
            RecentlyEpisode.objects.create(user=self.request.user, episode=episode)
class RecentlyWatchedViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]

    queryset = RecentlyWatched.objects.all()
    serializer_class = RecentlyWatchedSerializer

    def get_queryset(self):
        return RecentlyWatched.objects.filter(user=self.request.user)
    def perform_create(self, serializer):
        movie = serializer.validated_data['movie']
        series = serializer.validated_data['series']
        if movie:
            recent = RecentlyWatched.objects.filter(user=self.request.user,movie=movie)
            if recent.exists():
                recent.update(watched_at=timezone.now())
            else:
                recent = RecentlyWatched.objects.create(user=self.request.user,movie=movie)
        if series:
            recents = RecentlyWatched.objects.filter(user=self.request.user, series=series)
            if recents.exists():
                recents.update(watched_at=timezone.now())
            else:
                RecentlyWatched.objects.create(user=self.request.user, series=series)



class FolderViewSet(viewsets.ModelViewSet):
    permission_classes = [OnlyAdmin]
    queryset = Folder.objects.all()
    serializer_class = FolderSerializer

class FolderItemViewSet(viewsets.ModelViewSet):
    permission_classes = [OnlyAdmin]
    queryset = FolderItem.objects.all()
    serializer_class = FolderItemSerializer


class SearchView(generics.ListAPIView):
    def get(self,request):
        q = request.query_params.get('q','')
        if not q:
            return Response({'error':'q parameter is required'}, status=status.HTTP_400_BAD_REQUEST)
        movies = Movie.objects.filter(title__icontains=q)
        series = Series.objects.filter(title__icontains=q)

        content_list = []
        for item in movies:
            content_list.append({'title':item.title,
                                'poster_url':item.poster_url})
        for item in series:
            content_list.append({'title':item.title,
                                'poster_url':item.poster_url})

        return Response(content_list)


class RecommendationViewSet(generics.ListAPIView):
    def get_serializer_class(self):
        if self.request.query_params.get('movie_id'):
            return MovieSerializer
        if self.request.query_params.get('series_id'):
            return SeriesSerializer

    def get_queryset(self):
        movie_id = self.request.query_params.get('movie_id')
        series_id = self.request.query_params.get('series_id')
        if movie_id and series_id:
            raise ValidationError('You cannot specify both movie id and series id')
        if movie_id is None and series_id is None:
            raise ValidationError('You must specify either movie id or series id')
        if movie_id:
            movie = get_object_or_404(Movie, id=movie_id)
            genre = movie.genre.all()
            country = movie.country.all()
            language = movie.language.all()
            queryset = Movie.objects.filter(genre__in=genre)
            queryset = queryset.filter(country__in=country)
            queryset = queryset.filter(language__in=language)
            queryset = queryset.exclude(id=movie_id)
            queryset = queryset.distinct()
            queryset = queryset.order_by('?')[:10]
            return queryset
        if series_id:
            series = get_object_or_404(Series,id=series_id)
            genre = series.genre.all()
            country = series.country.all()
            language = series.language.all()
            queryset = Series.objects.filter(genre__in=genre)
            queryset = queryset.filter(country__in=country)
            queryset = queryset.filter(language__in=language)
            queryset = queryset.exclude(id=series_id)
            queryset = queryset.distinct()
            queryset = queryset.order_by('?')[:10]
            return queryset
