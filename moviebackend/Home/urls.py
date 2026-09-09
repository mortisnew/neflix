from django.urls import path
from . import views


urlpatterns = [
    path('home/popular-movies/', views.PopularMoviesView.as_view(), name='popular_movies'),
    path('home/popular-series/', views.PopularSeriesView.as_view(), name='popular_series'),
    path('home/top-rated-movies/', views.TopRatedMoviesView.as_view(), name='top_rated_movies'),
    path('home/top-rated-series/', views.TopRatedSeriesView.as_view(), name='top_rated_series'),
    path('home/latest-movies/',views.LatestMoviesView.as_view(), name='latest_movies'),
    path('home/latest-series/',views.LatestSeriesView.as_view(), name='latest_series'),
    path('home/genres/', views.GenresView.as_view(), name='genres'),
    path('home/countries/', views.CountriesView.as_view(), name='countries'),
    path('home/languages/', views.LanguagesView.as_view(), name='languages'),
]