from django.urls import path,include
from rest_framework import routers
from . import views

router = routers.DefaultRouter()

router.register('movies', views.MovieViewSet, basename='movie')
router.register('series', views.SeriesViewSet, basename='series')
router.register('seasons', views.SeasonViewSet, basename='season')
router.register('episodes', views.EpisodeViewSet, basename='episode')
router.register('comments', views.CommentViewSet, basename='comment')
router.register('ratings', views.RatingViewSet, basename='rating')
router.register('episode-streams', views.StreamEpisodeLinkViewSet, basename='episode_stream')
router.register('movie-streams', views.StreamMovieLinkViewSet, basename='movie_stream')
router.register('roles', views.RoleViewSet, basename='role')

urlpatterns = [
    path('',include(router.urls)),
]